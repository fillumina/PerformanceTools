package com.fillumina.performance.time.sample.iterator;

import com.fillumina.performance.executor.annotation.AnnotatedRunnableSetter;
import com.fillumina.performance.time.sample.TimeSampleBuilder;
import com.fillumina.performance.time.sample.TimeSampleCollector;
import com.fillumina.performance.util.ValueAssertion;
import com.fillumina.performance.util.collection.IndexedArrayMap;
import com.fillumina.performance.util.tname.TName;
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
 * This {@link PerformanceExecutor} uses many threads and
 * workers to run a code in a multi-threaded environment.
 * <p>
 * A <b>thread</b> is a code that race with all the other threads as the system
 for an available CPU to be executed on.<br>
 * A <b>worker</b> is a code that race for an available thread.<br>
 * All threads are executed concurrently (they might be interleaved by the
 * system scheduler if no physical CPU is available) but the workers have to wait
 * until the preceeding workers have finished to start being processed.
 * The amount of threads determines how many workers will be executed
 * concurrently, the amount of workers determines how many different task
 * should be performed.
 * <p>
 * <b>NOTES</b>
 * <ul>
 * <li>Taking performance measurement of a multi-threading process
 * is particularly tricky because it involves the OS scheduler and might be
 * influenced by synchronization and memory contention problems. Because of that
 * they are generally less precise than single-threaded ones;
 * <li>The tests run with this executor must be thread safe.
 * </ul>
 *
 * @author Francesco Illuminati
 */
public class MultiThreadPerformanceExecutor
        implements PerformanceExecutor, Serializable {
    private static final long serialVersionUID = 1L;

    private final int concurrencyLevel;
    private final int workerNumber;
    private final Quantity<IntervalUnit> timeout;

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
            final IndexedArrayMap<TName, Runnable> tests,
            final int[] iterations) {
        final TimeSampleCollector timeCollector =
                new TimeSampleCollector();

        int index = 0;
        for (Map.Entry<TName, Runnable> entry: tests.entrySet()) {
            final TName testName = entry.getKey();
            final Runnable runnable = entry.getValue();
            final int totalIterations = iterations[index] * workerNumber;

            AnnotatedRunnableSetter.INSTANCE
                    .onBeforeSample(runnable, totalIterations);

            final long elapsedNanoseconds = iterateOn(runnable, iterations[index]);

            AnnotatedRunnableSetter.INSTANCE
                    .onAfterSample(runnable, totalIterations);

            timeCollector.add(testName, elapsedNanoseconds, iterations[index]);

            index++;
        }

        return timeCollector;
    }

    private long iterateOn(Runnable runnable, int iterations) {
        final long timeoutMillis = (long)timeout.as(IntervalUnit.MILLISECONDS);
        final ExecutorService executor = createExecutor();

        final List<IteratingRunnable> tasks =
                createTasks(runnable, iterations);

        boolean alreadyTerminated = false;
        final long elapsed;

        final long time = System.nanoTime();

        for (IteratingRunnable task: tasks) {
            executor.execute(task);
        }

        executor.shutdown();

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

    private List<IteratingRunnable> createTasks(
            final Runnable runnable, final int iterations) {
        final List<IteratingRunnable> list = new ArrayList<>(workerNumber);
        final RunnableIterator iterator =
                RunnableIterator.DISPATCHER.getIterator(runnable);

        for(long i=0; i<workerNumber; i++) {
            list.add(new IteratingRunnable(iterator, iterations));
        }

        return list;
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

    private static class IteratingRunnable implements Runnable {
        private final RunnableIterator iterator;
        private final int iterations;

        public IteratingRunnable(final RunnableIterator iterator,
                final int iterations) {
            this.iterator = iterator;
            this.iterations = iterations;
        }

        @Override
        public void run() {
            iterator.iterate(iterations);
        }
    }
}
