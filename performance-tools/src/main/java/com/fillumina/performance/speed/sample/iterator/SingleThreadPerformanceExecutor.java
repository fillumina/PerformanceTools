package com.fillumina.performance.speed.sample.iterator;

import com.fillumina.performance.annotation.AnnotatedRunnableSetter;
import com.fillumina.performance.speed.sample.IterationTimeCollector;
import com.fillumina.performance.speed.sample.SpeedSample;
import com.fillumina.performance.util.TName;
import com.fillumina.performance.util.collection.LinkedMap;
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
     * @return a new instance of {@link SpeedSample}
     */
    @Override
    public SpeedSample executeTests(
            final LinkedMap<TName, Runnable> tests,
            final int[] iterations) {
        final IterationTimeCollector timeCollector =
                new IterationTimeCollector();

        final int actualFractions =
                calculateActualFractions(fractions, iterations);

        int[] iterationPerFraction =
                calculateIterationPerFraction(actualFractions, iterations);

        List<IterationData> testData =
                createTestData(tests, iterationPerFraction);

        for (Map.Entry<TName,Runnable> entry : tests.entrySet()) {
            TName name = entry.getKey();
            Runnable testable = entry.getValue();
            // to set the right order before shuffling
            timeCollector.add(name, 0, 0);
            AnnotatedRunnableSetter.INSTANCE.setUp(testable);
            RunnableIterator.INSTANCE.register(testable);
        }

        long elapsed;
        for (int f = 0; f < actualFractions; f++) {
            // minimizes inter-sample noise
            Collections.shuffle(testData);
            for (IterationData data : testData) {
                AnnotatedRunnableSetter.INSTANCE
                        .onBeforeSample(data.test, data.iteration);

                elapsed = RunnableIterator.INSTANCE
                        .measureIterationTime(data.test, data.iteration);

                AnnotatedRunnableSetter.INSTANCE
                        .onAfterSample(data.test, data.iteration);

                timeCollector.add(data.name, elapsed, data.iteration);
            }
            if (f + 1 < actualFractions) {
            }
        }

        for (Runnable testable : tests.values()) {
            AnnotatedRunnableSetter.INSTANCE.tearDown(testable);
        }

        return timeCollector.createPerformanceSample();
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
            IterationData id = new IterationData();
            id.name = entry.getKey();
            id.test = entry.getValue();
            id.iteration = iterationPerFraction[index];
            data[index] = id;
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

    private static class IterationData {
        TName name;
        volatile Runnable test;
        volatile int iteration;
    }
}
