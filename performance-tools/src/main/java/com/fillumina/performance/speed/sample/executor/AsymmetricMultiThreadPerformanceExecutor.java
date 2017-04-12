package com.fillumina.performance.speed.sample.executor;

import com.fillumina.performance.infrastructure.Testable;
import com.fillumina.performance.infrastructure.TestableController;
import com.fillumina.performance.speed.sample.IterationTimeCollector;
import com.fillumina.performance.speed.sample.SpeedSample;
import com.fillumina.performance.speed.sample.executor.AsymmetricTestable.Group;
import com.fillumina.performance.util.ValueAssertion;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * This executor calculates the performance of an asymmetric concurrent
 * calculations.
 *
 * @author Francesco Illuminati
 */
// TODO use @annotation over standard Testable
public class AsymmetricMultiThreadPerformanceExecutor
        implements PerformanceExecutor, Serializable {
    private static final long serialVersionUID = 1L;

    private final int concurrencyLevel;
    private final long timeout;
    private final TimeUnit unit;
    private volatile boolean running = true;

    public static MultiThreadPerformanceExecutorBuilder builder() {
        return new MultiThreadPerformanceExecutorBuilder();
    }

    /**
     * @see MultiThreadPerformanceExecutorBuilder
     */
    public AsymmetricMultiThreadPerformanceExecutor(final int concurrencyLevel,
            final long timeout,
            final TimeUnit unit) {
        ValueAssertion.isTrue(concurrencyLevel >= -1,
                "concurrency level must be > 0 or == -1 for unconstrained " +
                "threads; was " + concurrencyLevel);
        ValueAssertion.isTrue(timeout > 0,
                "timeout must be greater than 0; was " + timeout);
        ValueAssertion.isNotNull(unit != null, "unit");

        this.concurrencyLevel = concurrencyLevel;
        this.timeout = timeout;
        this.unit = unit;
    }

    @Override
    public SpeedSample executeTests(final LinkedHashMap<String, Testable> tests,
            final int[] bound) {

        assertAllTestsAreAsymmetric(tests);

        final IterationTimeCollector timeCollector =
                new IterationTimeCollector();

        int index = 0;
        for (Map.Entry<String,Testable> entry : tests.entrySet()) {
            final String testName = entry.getKey();
            final AsymmetricTestable testable = (AsymmetricTestable) entry.getValue();
            final int millis = bound[index];

            TestableIterator.INSTANCE.register(testable);

            final List<IteratingTestable> workerList = new ArrayList<>();

            int totalWorkers = 0;
            for (Group group : testable.getGroups()) {
                final int workers = group.getWorkers();
                totalWorkers += workers;
                final String groupName =
                        testName + "_" + group.getName() + "_" + workers;
                final Runnable runnable = group.getRunnable();

                for (int i=0; i<workers; i++) {
                    workerList.add(new IteratingTestable(groupName, runnable));
                }
            }

            TestableController.INSTANCE.setUp(testable);
            testable.onBeforeSample(totalWorkers);

            parallelExecution(workerList, millis);

            testable.onAfterSample(totalWorkers);
            TestableController.INSTANCE.tearDown(testable);

            for (IteratingTestable task : workerList) {
                timeCollector.add(task.name, task.elapsed, task.iterations);
            }

            index++;
        }

        return timeCollector.createPerformanceSample();
    }

    private long parallelExecution(final List<IteratingTestable> tasks,
            int millis) {
        boolean alreadyTerminated = false;
        final ExecutorService executor = createExecutor();

        final long time = System.nanoTime();

        running = true;
        for (IteratingTestable task: tasks) {
            executor.execute(task);
        }

        final long elapsed;
        try {
            Thread.sleep(millis);
            running = false;
            executor.shutdown();
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
        return new RuntimeException(
                "Task took longer than maximum time allowed " +
                "to complete: " + timeout + " " + unit, e);
    }

    private void assertAllTestsAreAsymmetric(
            LinkedHashMap<String, Testable> tests) {
        for (Entry<String,Testable> entry : tests.entrySet()) {
            String name = entry.getKey();
            Testable testable = entry.getValue();
            if (!(testable instanceof AsymmetricTestable)) {
                throw new IllegalArgumentException("test '" + name +
                        "' is not of type " +
                        AsymmetricTestable.class.getSimpleName());
            }

            AsymmetricTestable asymmetric = (AsymmetricTestable) testable;
            int workers = 0;
            for (Group group : asymmetric.getGroups()) {
                workers += group.getWorkers();
            }
            if (workers > concurrencyLevel) {
                throw new IllegalArgumentException("test '" + name +
                        "' requires more workers (" + workers +
                        ") than available concurrency level (" +
                        concurrencyLevel + ")");
            }
        }
    }

    private class IteratingTestable implements Runnable {
        private final String name;
        private final Runnable runnable;
        private int iterations = 0;
        private long elapsed;

        public IteratingTestable(final String name,
                final Runnable runnable) {
            this.name = name;
            this.runnable = runnable;
        }

        @Override
        public void run() {
            long time = System.nanoTime();
            while(running) {
                runnable.run();
                iterations++;
            }
            elapsed = System.nanoTime() - time;
        }
    }
}
