package com.fillumina.performance.sample.executor;

import com.fillumina.performance.sample.PerformanceSample;
import com.fillumina.performance.sample.Testable;
import com.fillumina.performance.util.Assertion;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * This {@link com.fillumina.performance.sample.executor.PerformanceExecutor}
 * uses many threads and
 * workers to test a code in a multi-threaded environment.<br>
 * A <b>thread</b> is a code that race with all the other threads in the system
 * for an available CPU to be executed in.<br>
 * A <b>worker</b> is a code that race for an available thread.<br>
 * All threads are executed concurrently (they are interleaved by the system
 * scheduler) but the workers have to wait
 * until the preceeding workers have finished to start being processed.
 * <p>
 * <b>NOTE:</b> The tests added to this executor will be executed by many threads
 * concurrently so take extra care with shared fields.
 *
 * @author Francesco Illuminati
 */
public class MultiThreadPerformanceExecutor
        implements PerformanceExecutor, Serializable {
    private static final long serialVersionUID = 1L;

    private final int concurrencyLevel;
    private final int workerNumber;
    private final long timeout;
    private final TimeUnit unit;

    public static MultiThreadPerformanceExecutorBuilder builder() {
        return new MultiThreadPerformanceExecutorBuilder();
    }

    /**
     * Wouldn't it be better to use the builder {@link #builder()} ?
     * This constructor is here just in case you wish to extend this class.
     */
    public MultiThreadPerformanceExecutor(final int concurrencyLevel,
            final int workerNumber,
            final long timeout,
            final TimeUnit unit) {
        Assertion.isTrue(concurrencyLevel >= -1,
                "concurrency level must be positive or < 1 for unconstrained " +
                "threads; was " + concurrencyLevel);
        Assertion.isTrue(workerNumber > 0,
                "worker number must be greater than 0; was " + workerNumber);
        Assertion.isTrue(timeout > 0,
                "timeout must be greater than 0; was " + timeout);
        Assertion.isNotNull(unit != null,
                "unit cannot be null");

        this.concurrencyLevel = concurrencyLevel;
        this.workerNumber = workerNumber;
        this.timeout = timeout;
        this.unit = unit;
    }

    @Override
    public PerformanceSample executeTests(final Map<String, Testable> tests,
            final int iterations) {
        final PerformanceSample performances =
                new PerformanceSample();

        for (Map.Entry<String, Testable> entry: tests.entrySet()) {
            final String msg = entry.getKey();
            final Testable testable = entry.getValue();

            final List<IteratingTestable> tasks = createTasks(testable, iterations);

            final long elapsedNanoseconds = iterateOn(tasks);

            performances.add(msg, elapsedNanoseconds, iterations);
        }

        return performances;
    }

    private List<IteratingTestable> createTasks(
            final Testable testable, final int iterations) {
        final List<IteratingTestable> list = new ArrayList<>(workerNumber);

        for(long i=0; i<workerNumber; i++) {
            list.add(new IteratingTestable(testable, iterations));
        }

        return list;
    }

    private long iterateOn(final List<IteratingTestable> tasks) {
        final ExecutorService executor = createExecutor();

        final long time = System.nanoTime();

        for (IteratingTestable task: tasks) {
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

    private static class IteratingTestable implements Runnable {
        private final Testable testable;
        private final int iterations;

        public IteratingTestable(final Testable testable, final int iterations) {
            this.testable = testable;
            this.iterations = iterations;
            testable.setUp();
        }

        @Override
        public void run() {
            testable.onBeforeSample(iterations);
            for (long i=0; i<iterations; i++) {
                if (testable.test() == this) {
                    // forces the return value of test() to be avaluated by
                    // the JVM so that the code will not be evicted by
                    // dead code optimizations.
                    throw new AssertionError();
                }
            }
        }
    }
}
