package com.fillumina.performance.time.sample.iterator;

import com.fillumina.performance.executor.annotation.AnnotatedRunnableSetter;
import com.fillumina.performance.time.sample.TimeSampleBuilder;
import com.fillumina.performance.time.sample.TimeSampleCollector;
import com.fillumina.performance.time.sample.iterator.ParallelTest.Group;
import com.fillumina.performance.util.Holder;
import com.fillumina.performance.util.ValueAssertion;
import com.fillumina.performance.util.collection.IndexedHashMap;
import com.fillumina.performance.util.tname.TName;
import com.fillumina.performance.util.unit.IntervalUnit;
import com.fillumina.performance.util.unit.Quantity;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * This executor calculates the performance of tasks that collaborates
 * in performing the same test and are executed each in a possibly different
 * number of threads.
 *
 * @author Francesco Illuminati
 */
// TODO adds total time per execution group
public class ParallelMultiThreadPerformanceExecutor
        implements PerformanceExecutor, Serializable {
    private static final long serialVersionUID = 1L;

    private final Quantity<IntervalUnit> timeout;
    private int concurrencyLevel;
    private volatile boolean running = true;

    public static MultiThreadPerformanceExecutorBuilder builder() {
        return new MultiThreadPerformanceExecutorBuilder();
    }

    /**
     * @see MultiThreadPerformanceExecutorBuilder
     */
    public ParallelMultiThreadPerformanceExecutor(final int concurrencyLevel,
            final Quantity<IntervalUnit> timeout) {
        ValueAssertion.isTrue(concurrencyLevel >= -1,
                "concurrency level must be > 0 or == -1 for unconstrained " +
                "threads; was " + concurrencyLevel);
        ValueAssertion.isTrue(timeout.getValue() > 0,
                "timeout must be greater than 0; was " + timeout);

        this.concurrencyLevel = concurrencyLevel;
        this.timeout = timeout;
    }

    @Override
    public TimeSampleBuilder executeIterations(
            final IndexedHashMap<TName, Runnable> tests,
            final int[] bound) {

        assertAllTestsAreAsymmetric(tests);

        int totalWorkersNeeded = calculateTotalWorkersNeeded(tests);
        if (concurrencyLevel >= 0 && totalWorkersNeeded > concurrencyLevel) {
            concurrencyLevel = totalWorkersNeeded;
        }

        final AnnotatedRunnableSetter runnableSetter =
                AnnotatedRunnableSetter.INSTANCE;

        final TimeSampleCollector timeCollector =
                new TimeSampleCollector();

        Holder.Integer index = new Holder.Integer();
        tests.forEach((TName testName, Runnable r) -> {
            final ParallelTest runnable = (ParallelTest) r;
            final int millis = bound[index.getValue()];

            List<IteratingRunnable> workerList =
                    createWorkers(runnable, testName);

            runnableSetter.setUp(runnable);
            runnableSetter.onBeforeSample(runnable, totalWorkersNeeded);

            parallelExecution(workerList, millis);

            runnableSetter.onAfterSample(runnable, totalWorkersNeeded);
            runnableSetter.tearDown(runnable);

            workerList.forEach((task) -> {
                timeCollector.add(task.name, task.elapsed, task.iterations);
            });

            index.incrementAndGet();
        });

        return timeCollector;
    }

    int getConcurrencyLevel() {
        return concurrencyLevel;
    }

    private List<IteratingRunnable> createWorkers(ParallelTest runnable,
            TName testName) {
        final List<IteratingRunnable> workerList = new ArrayList<>();
        for (Group group : runnable.getGroups()) {
            final int workers = group.getWorkers();
            final TName groupName = testName.append(group.getName());
            final Runnable test = group.getRunnable();

            for (int i=0; i<workers; i++) {
                workerList.add(new IteratingRunnable(groupName, test));
            }
        }
        return workerList;
    }

    private long parallelExecution(final List<IteratingRunnable> tasks,
            int millis) {
        final long timeoutMillis = (long)timeout.as(IntervalUnit.MILLISECONDS);
        boolean alreadyTerminated = false;
        final ExecutorService executor = createExecutor();

        final long time = System.nanoTime();

        running = true;
        for (IteratingRunnable task: tasks) {
            executor.execute(task);
        }

        final long elapsed;
        try {
            Thread.sleep(millis);
            running = false;
            executor.shutdown();
            alreadyTerminated = executor.awaitTermination(timeoutMillis,
                    TimeUnit.MILLISECONDS);
            elapsed = System.nanoTime() - time;
        } catch (InterruptedException e) {
            throw createTaskTookTooLongException(e);
        }

        if (!alreadyTerminated) {
            throw createTaskTookTooLongException(null);
        }

        return elapsed;
    }

    private ExecutorService createExecutor() {
        if (concurrencyLevel < 1) {
            return Executors.newCachedThreadPool();
        }
        return Executors.newFixedThreadPool(concurrencyLevel);
    }

    private RuntimeException createTaskTookTooLongException(final Exception e) {
        return new RuntimeException(
                "Task took longer than maximum time allowed " +
                "to complete: " + timeout, e);
    }

    private void assertAllTestsAreAsymmetric(IndexedHashMap<TName, Runnable> tests) {
        tests.forEach((TName name, Runnable runnable) -> {
            if (!(runnable instanceof ParallelTest)) {
                throw new IllegalArgumentException("test '" + name +
                        "' is not of type " +
                        ParallelTest.class.getSimpleName());
            }
        });
    }

    private int calculateTotalWorkersNeeded(IndexedHashMap<TName, Runnable> tests) {
        Holder.Integer workers = new Holder.Integer(0);
        tests.values().forEach(runnable -> {
            ParallelTest asymmetric = (ParallelTest) runnable;
            for (Group group : asymmetric.getGroups()) {
                workers.add(group.getWorkers());
            }
        });
        return workers.getValue();
    }

    private class IteratingRunnable implements Runnable {
        private final TName name;
        private final Runnable runnable;
        private int iterations = 0;
        private long elapsed;

        public IteratingRunnable(final TName name,
                final Runnable runnable) {
            this.name = name;
            this.runnable = runnable;
        }

        @Override
        public void run() {
            int it = iterations;
            Runnable r = runnable;
            long time = System.nanoTime();
            // TODO sure that polling a volatile variable is efficient/right?
            while(running) {
                r.run();
                it++;
            }
            // TODO use the interrupt() mechanism?
            iterations = it;
            elapsed = System.nanoTime() - time;
        }
    }
}
