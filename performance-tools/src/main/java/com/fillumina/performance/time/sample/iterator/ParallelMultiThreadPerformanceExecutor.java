package com.fillumina.performance.time.sample.iterator;

import com.fillumina.performance.executor.annotation.AnnotatedRunnableSetter;
import com.fillumina.performance.time.sample.TimeSampleBuilder;
import com.fillumina.performance.time.sample.TimeSampleCollector;
import com.fillumina.performance.time.sample.iterator.ParallelTest.Group;
import com.fillumina.performance.util.Holder;
import com.fillumina.performance.util.ValueAssertion;
import com.fillumina.performance.util.collection.IndexedHashMap;
import com.fillumina.performance.util.pathname.PathName;
import com.fillumina.performance.util.unit.IntervalUnit;
import com.fillumina.performance.util.unit.Quantity;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
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

    public static MultiThreadPerformanceExecutorBuilder builder() {
        return new MultiThreadPerformanceExecutorBuilder();
    }

    /** Unconstrained threads, 1 DAY timeout. */
    public ParallelMultiThreadPerformanceExecutor() {
        this(-1, IntervalUnit.DAYS.quantity(1));
    }

    /** Unconstrained threads. */
    public ParallelMultiThreadPerformanceExecutor(
            final Quantity<IntervalUnit> timeout) {
        this(-1, timeout);
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
            final IndexedHashMap<PathName, Runnable> tests,
            final int[] iterationArray) {

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
        tests.forEach((PathName testName, Runnable r) -> {
            final ParallelTest runnable = (ParallelTest) r;
            final int iterations = iterationArray[index.getValue()];

            List<IteratingRunnable> workerList =
                    createWorkers(testName, iterations, runnable);

            runnableSetter.setUp(runnable);
            runnableSetter.onBeforeSample(runnable, totalWorkersNeeded);

            parallelExecution(workerList);

            runnableSetter.onAfterSample(runnable, totalWorkersNeeded);
            runnableSetter.tearDown(runnable);

            workerList.forEach(task ->
                timeCollector.add(task.name, task.elapsed, task.iterations) );

            index.incrementAndGet();
        });

        return timeCollector;
    }

    int getConcurrencyLevel() {
        return concurrencyLevel;
    }

    private List<IteratingRunnable> createWorkers(PathName testName,
            int iterations, ParallelTest runnable) {
        final List<IteratingRunnable> workerList = new ArrayList<>();
        for (Group group : runnable.getGroups()) {
            final int workers = group.getWorkers();
            final PathName groupName = testName.append(group.getName());
            final Runnable test = group.getRunnable();

            for (int i=0; i<workers; i++) {
                final PathName pname = groupName.append(String.valueOf(i));
                final IteratingRunnable iteratingRunnable =
                        new IteratingRunnable(pname, iterations, test);
                workerList.add(iteratingRunnable);
            }
        }
        return workerList;
    }

    private long parallelExecution(final List<IteratingRunnable> tasks) {
        final long timeoutMillis = (long)timeout.as(IntervalUnit.MILLISECONDS);
        final ExecutorService executor = createExecutor();

        final long time = System.nanoTime();

        CountDownLatch startCountDownLatch = new CountDownLatch(1);
        CountDownLatch endCountDownLatch = new CountDownLatch(tasks.size());
        tasks.forEach(ir -> ir.setCountDownLatch(
                startCountDownLatch, endCountDownLatch));

        for (IteratingRunnable task: tasks) {
            executor.execute(task);
        }

        final long elapsed;
        try {
            Thread.sleep(200); // give it some time to initialize all runnables
            startCountDownLatch.countDown();
            endCountDownLatch.await(timeoutMillis, TimeUnit.MILLISECONDS);
            elapsed = System.nanoTime() - time;
            executor.shutdown();
            executor.awaitTermination(timeoutMillis, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw createTaskTookTooLongException(e);
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

    private void assertAllTestsAreAsymmetric(IndexedHashMap<PathName, Runnable> tests) {
        tests.forEach((PathName name, Runnable runnable) -> {
            if (!(runnable instanceof ParallelTest)) {
                throw new IllegalArgumentException("test '" + name +
                        "' is not of type " +
                        ParallelTest.class.getSimpleName());
            }
        });
    }

    private int calculateTotalWorkersNeeded(IndexedHashMap<PathName, Runnable> tests) {
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
        private final PathName name;
        private final Runnable runnable;
        private int iterations;
        private long elapsed;
        private CountDownLatch startCountDownLatch, endCountDownLatch;

        public IteratingRunnable(final PathName name,
                final int iterations,
                final Runnable runnable) {
            this.name = name;
            this.iterations = iterations;
            this.runnable = runnable;
        }

        void setCountDownLatch(CountDownLatch startCountDownLatch,
                CountDownLatch endCountDownLatch) {
            this.startCountDownLatch = startCountDownLatch;
            this.endCountDownLatch = endCountDownLatch;
        }

        @Override
        public void run() {
            final int it = iterations;
            Runnable r = runnable;
            try {
                startCountDownLatch.await();
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            long time = System.nanoTime();
            for (int i=0; i<it; i++) {
                r.run();
            }
            elapsed = System.nanoTime() - time;
            endCountDownLatch.countDown();
        }
    }
}
