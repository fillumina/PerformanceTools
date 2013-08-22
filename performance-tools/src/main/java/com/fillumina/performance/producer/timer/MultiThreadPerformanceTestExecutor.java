package com.fillumina.performance.producer.timer;

import com.fillumina.performance.producer.RunningLoopPerformances;
import com.fillumina.performance.producer.LoopPerformances;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * This {@link PerformanceTestExecutor} uses many threads and
 * workers to test a code in a multi-threaded environment.
 * Be careful to use {@link ThreadLocalRunnable} if you need to use
 * local variables otherwise they will be shared between all the
 * instances of the test!
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

    public static MultiThreadPerformanceTestExecutorBuilder builder() {
        return new MultiThreadPerformanceTestExecutorBuilder();
    }

    /** Wouldn't it be better to use the builder {@link #builder()} ? */
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
    public LoopPerformances executeTests(final long iterations,
            final Map<String, Runnable> tests) {
        final RunningLoopPerformances performances =
                new RunningLoopPerformances(iterations);

        for (Map.Entry<String, Runnable> entry: tests.entrySet()) {
            final String msg = entry.getKey();
            final Runnable runnable = entry.getValue();

            final List<IteratingRunnable> tasks = createTasks(runnable, iterations);

            final long elapsedNanoseconds = iterateOn(tasks);

            performances.add(msg, elapsedNanoseconds);
        }

        return performances.getLoopPerformances();
    }

    private List<IteratingRunnable> createTasks(
            final Runnable runnable, final long iterations) {
        final List<IteratingRunnable> list = new ArrayList<>(workerNumber);

        for(long i=0; i<workerNumber; i++) {
            list.add(new IteratingRunnable(runnable, iterations));
        }

        return list;
    }

    private long iterateOn(final List<IteratingRunnable> tasks) {
        final ExecutorService executor = createExecutor();

        final long time = System.nanoTime();

        for (IteratingRunnable task: tasks) {
            executor.execute(task);
        }

        executor.shutdown();

        boolean alreadyTerminated = false;
        final long elapsed;
        try {
            alreadyTerminated = executor.awaitTermination(timeout, unit);
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
        return new RuntimeException("Task took longer than maximum time allowed " +
                 "to complete: " + timeout + " " + unit, e);
    }

    private static class IteratingRunnable implements Runnable {
        private final Runnable test;
        private final long iterations;

        public IteratingRunnable(final Runnable test, final long iterations) {
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
}
