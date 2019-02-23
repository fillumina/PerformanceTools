package com.fillumina.performance.time.sample.iterator;

import com.fillumina.performance.executor.annotation.AnnotatedRunnableSetter;
import com.fillumina.performance.time.sample.TimeSampleBuilder;
import com.fillumina.performance.time.sample.TimeSampleCollector;
import com.fillumina.performance.util.ValueAssertion;
import com.fillumina.performance.util.collection.IndexedHashMap;
import com.fillumina.performance.util.pathname.PathName;
import com.fillumina.performance.util.unit.IntervalUnit;
import com.fillumina.performance.util.unit.Quantity;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * This executor takes statistics from each thread executing the same test.
 * It will also execute the test on ingle thread as a comparison.
 * <p>
 * This {@link PerformanceExecutor} uses many threads and
 * workers to run a code as a multi-threaded environment.
 * <p>
 * A <b>thread</b> is a code that races with all the other threads in the system
 * for CPU execution time.<br>
 * A <b>worker</b> is a code that races for an available thread.<br>
 * All threads are executed concurrently (they might be interleaved by the
 * operative system scheduler) but the workers
 * have to wait until all the preceding workers have finished to start being
 * processed.
 * <p>
 * <b>NOTES</b>
 * <ul>
 * <li>Taking performance measurement of a multi-threading process
 * is particularly tricky because it involves the OS scheduler and might be
 * influenced by synchronization and memory contention problems. Because of that
 * they are generally less accurate than single-threaded ones;
 * <li>The tests run with this executor will be executed by many threads
 * concurrently so they <b>must</b> be thread safe.
 * </ul>
 *
 * @author Francesco Illuminati
 */
public class SingleTestMultiThreadPerformanceExecutor
        implements PerformanceExecutor, Serializable {
    private static final long serialVersionUID = 1L;

    private final int concurrencyLevel;
    private final int workerNumber;
    private final Quantity<IntervalUnit> timeout;

    public static MultiThreadPerformanceExecutorBuilder builder() {
        return new MultiThreadPerformanceExecutorBuilder();
    }

    /**
     * @see MultiThreadPerformanceExecutorBuilder
     */
    public SingleTestMultiThreadPerformanceExecutor(
            final int concurrencyLevel,
            final int workerNumber,
            final Quantity<IntervalUnit> timeout) {
        ValueAssertion.isTrue(concurrencyLevel >= -1,
                "concurrency level must be positive or < 1 for unconstrained " +
                "threads; was " + concurrencyLevel);
        ValueAssertion.isTrue(workerNumber > 0,
                "worker number must be greater than 0; was " + workerNumber);
        ValueAssertion.isTrue(timeout.getValue() > 0,
                "timeout must be greater than 0; was " + timeout);

        this.concurrencyLevel = concurrencyLevel;
        this.workerNumber = workerNumber;
        this.timeout = timeout;
    }

    @Override
    public TimeSampleBuilder executeIterations(
            final IndexedHashMap<PathName, Runnable> tests,
            final int[] iterations) {
        if (tests.isEmpty() || tests.size() != 1) {
            throw new IllegalArgumentException(
                    "This executor works only with one single test");
        }

        // get the first (and only) run
        final Map.Entry<PathName,Runnable> entry =
                tests.entrySet().iterator().next();
        final PathName testName = entry.getKey();
        final Runnable testable = entry.getValue();
        final int iteration = iterations[0];

        final TimeSampleCollector timeCollector = new TimeSampleCollector();

        AnnotatedRunnableSetter.INSTANCE.setUp(testable);

        // run the single thread test to use as a baseline and to warmup the code
        final RunnableIterator iterator =
                RunnableIterator.DISPATCHER.getIterator(testable);
        final IteratingRunnable singleTask =
                new IteratingRunnable(iterator, iteration);
        singleTask.run();
        long singleThreadElapsed = singleTask.getElapsedTimeNs();
        timeCollector.add(testName.append("single"),
                singleThreadElapsed, iteration);

        // parallel tests are less accurate and need more time
        final int parallelIterations = iteration * 2;

        // run the parallel execution
        final List<IteratingRunnable> tasks =
                createTasks(testable, parallelIterations);

        long parallelElapsed = parallelExecution(tasks);

        int taskNumber = 0;
        for (IteratingRunnable task : tasks) {
            timeCollector.add(testName.append("" + taskNumber),
                    task.getElapsedTimeNs(), parallelIterations);
            taskNumber++;
        }

        timeCollector.add(testName.append("parallel"), parallelElapsed,
                tasks.size() * parallelIterations);

        AnnotatedRunnableSetter.INSTANCE.tearDown(testable);

        return timeCollector;
    }

    private List<IteratingRunnable> createTasks(
            final Runnable testable, final int iterations) {
        final List<IteratingRunnable> list = new ArrayList<>(workerNumber);
        final RunnableIterator iterator =
                RunnableIterator.DISPATCHER.getIterator(testable);

        for(long i=0; i<workerNumber; i++) {
            list.add(new IteratingRunnable(iterator, iterations));
        }

        return list;
    }

    private long parallelExecution(final List<IteratingRunnable> tasks) {
        final long timeoutMillis = (long)timeout.as(IntervalUnit.MILLISECONDS);
        final ExecutorService executor = createExecutor();

        final long time = System.nanoTime();

        for (IteratingRunnable task: tasks) {
            executor.execute(task);
        }

        executor.shutdown();

        boolean alreadyTerminated = false;
        final long elapsed;
        try {
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
        return new RuntimeException("Task took longer than maximum time allowed " +
                 "to complete: " + timeout, e);
    }

    private static class IteratingRunnable implements Runnable {
        private final RunnableIterator iterator;
        private final int iterations;

        private long elapsedTime;

        public IteratingRunnable(final RunnableIterator iterator,
                final int iterations) {
            this.iterator = iterator;
            this.iterations = iterations;
        }

        public long getElapsedTimeNs() {
            return elapsedTime;
        }

        @Override
        public void run() {
            AnnotatedRunnableSetter.INSTANCE
                    .onBeforeSample(iterator.getRunnable(), iterations);

            elapsedTime = iterator.measureIterationTimeNs(iterations);

            AnnotatedRunnableSetter.INSTANCE
                    .onAfterSample(iterator.getRunnable(), iterations);
        }
    }
}
