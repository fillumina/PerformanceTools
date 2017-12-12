package com.fillumina.performance.time.sample.iterator;

import com.fillumina.performance.executor.annotation.AnnotatedRunnableSetter;
import com.fillumina.performance.time.sample.TimeSampleBuilder;
import com.fillumina.performance.time.sample.TimeSampleCollector;
import com.fillumina.performance.util.collection.IndexedArrayMap;
import com.fillumina.performance.util.tname.TName;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * This {@link PerformanceExecutor} uses a single thread and interleaves
 * the run executions so to average the effect of a disturbance in the
 * performances offered by the system.
 * *
 * @author Francesco Illuminati
 */
public class SingleThreadPerformanceExecutor
        implements PerformanceExecutor, Serializable {

    private static final long serialVersionUID = 1L;
    public static final SingleThreadPerformanceExecutor INSTANCE =
            new SingleThreadPerformanceExecutor();

    private final int fractions;

    public SingleThreadPerformanceExecutor() {
        this(1); // safest choice
    }

    /**
     * Interleaves tests execution so to average disturbing events.
     * There are known drawbacks in executing more than one
     * run at the same time: they could interfere with each other
     * (directly by contending a common resource or indirectly by influencing
     * JVM memory manager or code optimization).
     *
     * @param fractions How many times each run switch to the next to average
                  system's disturbances
     */
    public SingleThreadPerformanceExecutor(final int fractions) {
        this.fractions = fractions;
    }

    /**
     * Executes the given tests for the required number of iterations and
     * returns a sample.
     *
     * @param iterations times a run must be executed
     * @param tests      name and code of tests
     * @return a new instance of {@link AverageTimeSample}
     */
    @Override
    public TimeSampleBuilder executeIterations(
            final IndexedArrayMap<TName, Runnable> tests,
            final int[] iterations) {

        final int actualFractions =
                calculateActualFractions(fractions, iterations);

        int[] iterationPerFraction =
                calculateIterationPerFraction(actualFractions, iterations);

        List<IterationData> testData =
                createTestData(tests, iterationPerFraction);

        final TimeSampleCollector timeCollector =
                new TimeSampleCollector();
        // to set the right order before shuffling
        for (TName name : tests.keySet()) {
            timeCollector.add(name, 0, 0);
        }

        setupTests(tests);

        long elapsed;
        for (int f = 0; f < actualFractions; f++) {
            // minimizes inter-sample noise
            Collections.shuffle(testData);
            for (IterationData data : testData) {
                elapsed = data.iterate();
                timeCollector.add(data.name, elapsed, data.iterations);
            }
        }

        tearDownTests(tests);

        return timeCollector;
    }

    private int[] calculateIterationPerFraction(int fractions,
            int[] iterations) {
        int[] iterationsPerFraction = new int[iterations.length];
        for (int i = 0; i < iterations.length; i++) {
            iterationsPerFraction[i] = iterations[i] / fractions;

            // check for too few iterations
            if (iterationsPerFraction[i] == 0) {
                iterationsPerFraction[i] = 1;
            }
        }
        return iterationsPerFraction;
    }

    private static List<IterationData> createTestData(
            Map<TName, Runnable> tests,
            int[] iterationPerFraction) {
        IterationData[] data = new IterationData[iterationPerFraction.length];
        int index = 0;
        for (Map.Entry<TName, Runnable> entry : tests.entrySet()) {
            TName name = entry.getKey();
            final Runnable runnable = entry.getValue();
            RunnableIterator iterator =
                    RunnableIterator.DISPATCHER.getIterator(runnable);
            int iterations = iterationPerFraction[index];
            data[index] = new IterationData(name, iterator, iterations);
            index++;
        }
        return Arrays.asList(data);
    }

    private static int calculateActualFractions(int fractions, int[] iterations) {
        int minIterations = Integer.MAX_VALUE;
        for (int it : iterations) {
            if (it < minIterations) {
                minIterations = it;
            }
        }
        return minIterations < fractions ? 1 : fractions;
    }

    private void setupTests(IndexedArrayMap<TName, Runnable> tests) {
        for (Runnable testable : tests.values()) {
            AnnotatedRunnableSetter.INSTANCE.setUp(testable);
        }
    }

    private void tearDownTests(IndexedArrayMap<TName, Runnable> tests) {
        for (Runnable testable : tests.values()) {
            AnnotatedRunnableSetter.INSTANCE.tearDown(testable);
        }
    }

    private static class IterationData {
        private final TName name;
        private final RunnableIterator iterator;
        private final int iterations;

        public IterationData(TName name, RunnableIterator iterator,
                int iterations) {
            this.name = name;
            this.iterator = iterator;
            this.iterations = iterations;
        }

        long iterate() {
            AnnotatedRunnableSetter.INSTANCE
                    .onBeforeSample(iterator.getRunnable(), iterations);

            // it has problem with debuggers
//            long elapsed = iterator.measureIterationTimeNsInNewThread(iterations);

            // ok with debuggers
            long elapsed = iterator.measureIterationTimeNs(iterations);

            AnnotatedRunnableSetter.INSTANCE
                    .onAfterSample(iterator.getRunnable(), iterations);

            return elapsed;
        }

    }
}
