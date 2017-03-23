package com.fillumina.performance.speed.sample.executor;

import com.fillumina.performance.infrastructure.Testable;
import com.fillumina.performance.infrastructure.TestableController;
import com.fillumina.performance.speed.sample.IterationTimeCollector;
import com.fillumina.performance.speed.sample.SpeedSample;
import com.fillumina.performance.util.ValueAssertion;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * This {@link PerformanceExecutor} uses many threads and
 * workers to test a code in a multi-threaded environment.
 * <p>
 * A <b>thread</b> is a code that race with all the other threads in the system
 * for an available CPU to be executed on.<br>
 * A <b>worker</b> is a code that race for an available thread.<br>
 * All threads are executed concurrently (they might be interleaved by the
 * system scheduler if no physical CPU is available) but the workers have to wait
 * until the preceeding workers have finished to start being processed.
 * <p>
 * <b>NOTES</b>
 * <ul>
 * <li>Taking performance measurement of a multi-threading process
 * is particularly tricky because it involves the OS scheduler and might be
 * influenced by synchronization and memory contention problems. Because of that
 * they are generally less precise of single-threaded ones;
 * <li>The tests run with this executor will be executed by many threads
 * concurrently so they must be thread safe.
 * </ul>
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
     *
     * @param concurrencyLevel number of threads available
     * @param workerNumber number of workers concurring for a thread
     * @param timeout if < 0 the disables timeout
     * @param unit time unit for timeout
     *
     * @see MultiThreadPerformanceExecutorBuilder
     */
    public MultiThreadPerformanceExecutor(final int concurrencyLevel,
            final int workerNumber,
            final long timeout,
            final TimeUnit unit) {
        ValueAssertion.isTrue(concurrencyLevel >= -1,
                "concurrency level must be positive or < 1 for unconstrained " +
                "threads; was " + concurrencyLevel);
        ValueAssertion.isTrue(workerNumber > 0,
                "worker number must be greater than 0; was " + workerNumber);
        ValueAssertion.isTrue(timeout > 0,
                "timeout must be greater than 0; was " + timeout);
        ValueAssertion.isNotNull(unit != null, "unit");

        this.concurrencyLevel = concurrencyLevel;
        this.workerNumber = workerNumber;
        this.timeout = timeout;
        this.unit = unit;
    }

    @Override
    public SpeedSample executeTests(final Map<String, Testable> tests,
            final int[] iterations) {
        final IterationTimeCollector timeCollector =
                new IterationTimeCollector();

        int index = 0;
        for (Map.Entry<String, Testable> entry: tests.entrySet()) {
            final String testName = entry.getKey();
            final Testable testable = entry.getValue();
            final int totalIterations = iterations[index] * workerNumber;

            TestableController.INSTANCE.setUp(testable);
            testable.onBeforeSample(totalIterations);

            final List<IteratingTestable> tasks =
                    createTasks(testable, iterations[index]);

            final long elapsedNanoseconds = iterateOn(tasks);

            testable.onAfterSample(totalIterations);
            TestableController.INSTANCE.tearDown(testable);

            timeCollector.add(testName, elapsedNanoseconds, iterations[index]);

            index++;
        }

        return timeCollector.createPerformanceSample();
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
        }

        @Override
        public void run() {
            for (int i=0; i<iterations; i++) {
                testable.test();
            }
        }
    }
}
