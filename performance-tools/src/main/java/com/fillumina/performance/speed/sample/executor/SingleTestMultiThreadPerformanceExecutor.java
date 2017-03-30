package com.fillumina.performance.speed.sample.executor;

import com.fillumina.performance.infrastructure.Testable;
import com.fillumina.performance.infrastructure.TestableController;
import com.fillumina.performance.speed.sample.IterationTimeCollector;
import com.fillumina.performance.speed.sample.SpeedSample;
import com.fillumina.performance.util.ValueAssertion;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * This executor takes statistics from each thread executing the test
 * so to evaluate if the code under test is efficiently parallel.
 * It will also execute the test on a single thread to check
 * the parallelization level.
 * <p>
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
public class SingleTestMultiThreadPerformanceExecutor
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
     * @see MultiThreadPerformanceExecutorBuilder
     */
    public SingleTestMultiThreadPerformanceExecutor(final int concurrencyLevel,
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
    public SpeedSample executeTests(final LinkedHashMap<String, Testable> tests,
            final int[] iterations) {
        if (tests.isEmpty() || tests.size() != 1) {
            throw new IllegalArgumentException(
                    "This executor works only with one single test");
        }

        // get the first test
        final Map.Entry<String,Testable> entry =
                tests.entrySet().iterator().next();
        final String testName = entry.getKey();
        final Testable testable = entry.getValue();
        final int iteration = iterations[0];

        final IterationTimeCollector timeCollector =
                new IterationTimeCollector();

        TestableController.INSTANCE.setUp(testable);

        // run first the single thread to use as a baseline
        final IteratingTestable singleTask =
                new IteratingTestable(testable, iteration);
        singleTask.run();
        long singleThreadElapsed = singleTask.getElapsedTimeNs();
        timeCollector.add(testName + "_single", singleThreadElapsed, iteration);

        final int parallelIterations = iteration * 2;

        // run the parallel execution
        final List<IteratingTestable> tasks =
                createTasks(testable, parallelIterations);

        long parallelElapsed = parallelExecution(tasks);

        int taskNumber = 0;
        for (IteratingTestable task : tasks) {
            timeCollector.add(testName + "_" + taskNumber,
                    task.getElapsedTimeNs(), parallelIterations);
            taskNumber++;
        }

        timeCollector.add(testName + "_parallel", parallelElapsed,
                tasks.size() * parallelIterations);

        TestableController.INSTANCE.tearDown(testable);

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

    private long parallelExecution(final List<IteratingTestable> tasks) {
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

        private long elapsedTime;

        public IteratingTestable(final Testable testable, final int iterations) {
            this.testable = testable;
            this.iterations = iterations;
        }

        public long getElapsedTimeNs() {
            return elapsedTime;
        }

        @Override
        public void run() {
            testable.onBeforeSample(iterations);
            final long startTime = System.nanoTime();
            for (long i=0; i<iterations; i++) {
                testable.test();
            }
            elapsedTime = System.nanoTime() - startTime;
            testable.onAfterSample(iterations);
        }
    }
}
