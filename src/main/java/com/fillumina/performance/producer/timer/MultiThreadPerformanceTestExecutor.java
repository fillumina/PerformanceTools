package com.fillumina.performance.producer.timer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * This {@link PerformanceTestExecutor} can use many threads and
 * workers to test a code in a multi-threaded environment.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MultiThreadPerformanceTestExecutor
        implements PerformanceTestExecutor, Serializable {
    private static final long serialVersionUID = 1L;

    private final int concurrencyLevel;
    private final int workerNumber;
    private final int timeout;
    private final TimeUnit unit;

    public static class Builder {
        private int concurrencyLevel = -1;
        private int workerNumber;
        private int timeout = 5;
        private TimeUnit unit = TimeUnit.SECONDS;

        /** Number of threads to use */
        public Builder setConcurrencyLevel(final int concurrencyLevel) {
            this.concurrencyLevel = concurrencyLevel;
            return this;
        }

        /** Creates as many threads as required */
        public Builder setUnlimitedConcurrencyLevel() {
            this.concurrencyLevel = -1;
            return this;
        }

        /**
         * Number of workers. i.e. instances of code that race for a
         * free thread to be executed.
         */
        public Builder setWorkerNumber(final int workerNumber) {
            this.workerNumber = workerNumber;
            return this;
        }

        public Builder setTimeout(final int timeout, final TimeUnit unit) {
            this.timeout = timeout;
            this.unit = unit;
            return this;
        }

        public MultiThreadPerformanceTestExecutor build() {
            if (workerNumber < 0 || timeout < 0 ||
                    unit == null) {
                throw new IllegalArgumentException();
            }
            return new MultiThreadPerformanceTestExecutor(
                    concurrencyLevel, workerNumber, timeout, unit);
        }
    }

    public MultiThreadPerformanceTestExecutor(final int concurrencyLevel,
            final int workerNumber,
            final int timeout,
            final TimeUnit unit) {
        assert concurrencyLevel >= -1;
        assert workerNumber > 0;
        assert timeout > 0;
        assert unit != null;

        this.concurrencyLevel = concurrencyLevel;
        this.workerNumber = workerNumber;
        this.timeout = timeout;
        this.unit = unit;
    }

    @Override
    public LoopPerformances executeTests(final int iterations,
            final Map<String, Runnable> tests) {
        final RunningLoopPerformances performances =
                new RunningLoopPerformances(iterations);

        for (Map.Entry<String, Runnable> entry: tests.entrySet()) {
            final String msg = entry.getKey();
            final Runnable runnable = entry.getValue();

            final List<IteratingRunnable> tasks = createTasks(runnable, iterations);

            final long time = System.nanoTime();

            iterateOn(tasks);

            performances.add(msg, System.nanoTime() - time);
        }

        return performances.getLoopPerformances();
    }

    private void iterateOn(final List<IteratingRunnable> tasks) {
        final ExecutorService executor = createExecutor();

        for (IteratingRunnable task: tasks) {
            executor.execute(task);
        }

        executor.shutdown();

        boolean alreadyTerminated = false;
        try {
            alreadyTerminated = executor.awaitTermination(timeout, unit);
        } catch (InterruptedException e) {
            throw createTaskTookTooLongException(timeout, unit, e);
        }
        if (!alreadyTerminated) {
            throw createTaskTookTooLongException(timeout, unit, null);
        }
    }

    private ExecutorService createExecutor() {
        if (concurrencyLevel < 1) {
            return Executors.newCachedThreadPool();
        }
        return Executors.newFixedThreadPool(concurrencyLevel);
    }

    private RuntimeException createTaskTookTooLongException(
            final int timeout, final TimeUnit unit, final Exception e) {
        return new RuntimeException("Task took longer than maximum time allowed " +
                 "to complete: " + timeout + " " + unit, e);
    }

    private static class IteratingRunnable implements Runnable {
        private final Runnable test;
        private final long iterations;

        public IteratingRunnable(Runnable test, int iterations) {
            this.test = test;
            this.iterations = iterations;
        }

        @Override
        public void run() {
            for (long i=0; i<iterations; i++) {
                test.run();
            }
        }
    }

    private List<IteratingRunnable> createTasks(
            final Runnable runnable, final int iterations) {
        final List<IteratingRunnable> list = new ArrayList<>(workerNumber);

        for(long i=0; i<workerNumber; i++) {
            list.add(new IteratingRunnable(runnable, iterations));
        }

        return list;
    }

}
