package com.fillumina.performance.time.sample.iterator;

import com.fillumina.performance.executor.annotation.AnnotatedRunnableSetter;
import com.fillumina.performance.time.sample.TimeSampleBuilder;
import com.fillumina.performance.time.sample.TimeSampleCollector;
import com.fillumina.performance.time.sample.iterator.ParallelTest.ConcurrentRunnable;
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
import java.util.concurrent.Future;
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
     *
     * @param concurrencyLevel The number of thread created:
     *           <ul>
     *           <li>if -1 then unbounded threads will be used
     *           <li>if 0 then there will be as many threads as needed workers
     *           <li>otherwise the given number of threads will be used
     *           </ul>
     * @param timeout the maximum allowed time for the test to be completed
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
        assertIterations(iterationArray, tests);

        int totalWorkersNeeded = calculateTotalWorkersNeeded(tests);
        if (concurrencyLevel == 0 && totalWorkersNeeded > concurrencyLevel) {
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
            if (concurrencyLevel > 0 && concurrencyLevel < workerList.size()) {
                throw new IllegalArgumentException("parallel test requires " +
                        workerList.size() + " threads but has " + concurrencyLevel);
            }

            runnableSetter.setUp(runnable);
            runnableSetter.onBeforeSample(runnable, totalWorkersNeeded);

            parallelExecution(iterations, workerList);

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
            final ConcurrentRunnable test = group.getConcurrentRunnable();

            for (int i=0; i<workers; i++) {
                final PathName pname = groupName.append(String.valueOf(i));
                final IteratingRunnable iteratingRunnable =
                        new IteratingRunnable(pname, i, iterations, test);
                workerList.add(iteratingRunnable);
            }
        }
        return workerList;
    }

    private long parallelExecution(final int iterations,
            final List<IteratingRunnable> tasks) {
        final long timeoutMillis = (long)timeout.as(IntervalUnit.MILLISECONDS);
        final ExecutorService executor = createExecutor();

        CountDownLatch setupCountDownLatch = new CountDownLatch(tasks.size());
        CountDownLatch startCountDownLatch = new CountDownLatch(1);
        CountDownLatch endCountDownLatch = new CountDownLatch(tasks.size());

        tasks.forEach(ir -> ir.setCountDownLatch(
                setupCountDownLatch, startCountDownLatch, endCountDownLatch));

        List<Future<?>> futures = new ArrayList<>(tasks.size());
        try {
            for (IteratingRunnable task : tasks) {
                futures.add(executor.submit(task));
            }
            if (!setupCountDownLatch.await(timeoutMillis, TimeUnit.MILLISECONDS)) {
                throw createTaskTookTooLongException(null);
            }
            startCountDownLatch.countDown();
            final long time = System.nanoTime();
            if (iterations > 0) {
                if (!endCountDownLatch.await(timeoutMillis, TimeUnit.MILLISECONDS)) {
                    throw createTaskTookTooLongException(null);
                }
                WorkerTasks.checkFailures(futures);
            } else {
                executor.shutdown();
                executor.awaitTermination(timeoutMillis, TimeUnit.MILLISECONDS);
                for (Future<?> future : futures) {
                    if (future.isDone()) {
                        WorkerTasks.checkFailures(java.util.Collections.singletonList(future));
                    }
                }
            }
            return System.nanoTime() - time;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw createTaskTookTooLongException(e);
        } finally {
            executor.shutdownNow();
        }
    }

    protected class IteratingRunnable implements Runnable {
        private final PathName name;
        private final int index;
        private final ConcurrentRunnable runnable;
        private int iterations;
        private long elapsed;
        private CountDownLatch setupCountDownLatch;
        private CountDownLatch startCountDownLatch;
        private CountDownLatch endCountDownLatch;

        public IteratingRunnable(final PathName name,
                final int index,
                final int iterations,
                final ConcurrentRunnable runnable) {
            this.name = name;
            this.index = index;
            this.iterations = iterations;
            this.runnable = runnable;
        }

        void setCountDownLatch(CountDownLatch setupCountDownLatch,
                CountDownLatch startCountDownLatch,
                CountDownLatch endCountDownLatch) {
            this.setupCountDownLatch = setupCountDownLatch;
            this.startCountDownLatch = startCountDownLatch;
            this.endCountDownLatch = endCountDownLatch;
        }

        @Override
        public void run() {
            int it = 0;
            final int idx = index;
            ConcurrentRunnable r = runnable;
            setupCountDownLatch.countDown();
            try {
                startCountDownLatch.await();
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            final long time = System.nanoTime();
            try {
                if (iterations > 0) {
                    it = iterations;
                    for (; it != 0; it--) {
                        r.run(idx);
                    }
                } else {
                    while (true) {
                        r.run(idx);
                        it++;
                        if (Thread.currentThread().isInterrupted()) {
                            iterations = it;
                            break;
                        }
                    }
                }
                elapsed = System.nanoTime() - time;
            } finally {
                endCountDownLatch.countDown();
            }
        }
    }

    protected ExecutorService createExecutor() {
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

    protected void assertIterations(int[] iterations,
            IndexedHashMap<PathName, Runnable> tests) {
        final int length = iterations.length;
        if (length == 0 || length > tests.size()) {
            throw new IllegalArgumentException(
                    "invalid iterations array size of " + length);
        }
        for (int i=0; i < iterations.length; i++) {
            if (iterations[i] < 0) {
                throw new IllegalArgumentException(
                        "invalid iterations index[" + i + "] = " +
                                iterations[i]);
            }
        }
    }

    private void assertAllTestsAreAsymmetric(
            IndexedHashMap<PathName, Runnable> tests) {
        tests.forEach((PathName name, Runnable runnable) -> {
            if (!(runnable instanceof ParallelTest)) {
                throw new IllegalArgumentException("test '" + name +
                        "' is not of type " +
                        ParallelTest.class.getSimpleName());
            }
        });
    }

    private int calculateTotalWorkersNeeded(
            IndexedHashMap<PathName, Runnable> tests) {
        Holder.Integer workers = new Holder.Integer(0);
        tests.values().forEach(runnable -> {
            ParallelTest asymmetric = (ParallelTest) runnable;
            for (Group group : asymmetric.getGroups()) {
                workers.add(group.getWorkers());
            }
        });
        return workers.getValue();
    }
}
