package com.fillumina.performance.speed.sample.executor;

import com.fillumina.performance.infrastructure.Testable;
import com.fillumina.performance.infrastructure.TestableController;
import com.fillumina.performance.speed.sample.IterationTimeCollector;
import com.fillumina.performance.speed.sample.SpeedSample;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * This {@link PerformanceExecutor} uses a single thread and interleaves
 * the test executions so to average the effect of a disturbance in the
 * performances offered by the system.
 *
 * @author Francesco Illuminati
 */
public class SingleThreadPerformanceExecutor
        implements PerformanceExecutor, Serializable {

    private static final long serialVersionUID = 1L;

    private final int fractions;

    public SingleThreadPerformanceExecutor() {
        this(1); // safest choice
    }

    /**
     * Interleaves tests execution so to average disturbing events.
     * There are known drawbacks in executing more than one
     * test at the same time: they could interfere with each other
     * (directly by contending a common resource or indirectly by influencing
     * JVM memory manager or code optimization).
     *
     * @param fractions How many times each test switch to the next to average
     *                  system's disturbances
     */
    public SingleThreadPerformanceExecutor(final int fractions) {
        this.fractions = fractions;
    }

    /**
     * Executes the given tests for the required number of iterations and
     * returns a sample.
     *
     * @param iterations times a test must be executed
     * @param tests      name and code of tests
     * @return a new instance of {@link SpeedSample}
     */
    @Override
    public SpeedSample executeTests(final Map<String, Testable> tests,
            final int[] iterations) {
        final IterationTimeCollector timeCollector =
                new IterationTimeCollector();

        final int actualFractions =
                calculateActualFractions(fractions, iterations);

        int[] iterationPerFraction =
                calculateIterationPerFraction(actualFractions, iterations);

        List<IterationData> testData =
                createTestData(tests, iterationPerFraction);

        for (int f = 0; f < actualFractions; f++) {
            for (IterationData data : testData) {
                TestableController.INSTANCE.setUp(data.test);
                data.test.onBeforeSample(data.iteration);

                final long startTime = System.nanoTime();

                for (int t = 0; t < data.iteration; t++) {
                    data.test.test();
                }

                final long elapsed = System.nanoTime() - startTime;

                data.test.onAfterSample(data.iteration);
                TestableController.INSTANCE.tearDown(data.test);

                timeCollector.add(data.name, elapsed, data.iteration);
            }
            if (f + 1 < actualFractions) {
                // to minimize inter-test noise (at last to keep insert order)
                Collections.shuffle(testData);
            }
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

    private List<IterationData> createTestData(
            Map<String, Testable> tests,
            int[] iterationPerFraction) {
        IterationData[] data = new IterationData[iterationPerFraction.length];
        int index = 0;
        for (Map.Entry<String, Testable> entry : tests.entrySet()) {
            IterationData id = new IterationData();
            id.name = entry.getKey();
            id.test = entry.getValue();
            id.iteration = iterationPerFraction[index];
            data[index] = id;
            index++;
        }
        return Arrays.asList(data);
    }

    private int calculateActualFractions(int fractions, int[] iterations) {
        int minIterations = Integer.MAX_VALUE;
        for (int it : iterations) {
            if (it < minIterations) {
                minIterations = it;
            }
        }
        return minIterations < fractions ? 1 : fractions;
    }

    private static class IterationData {
        String name;
        Testable test;
        int iteration;
    }
}
