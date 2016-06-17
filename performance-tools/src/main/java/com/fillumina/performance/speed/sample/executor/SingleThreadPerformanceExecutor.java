package com.fillumina.performance.speed.sample.executor;

import com.fillumina.performance.speed.sample.IterationTimeCollector;
import com.fillumina.performance.speed.sample.PerformanceSample;
import com.fillumina.performance.speed.sample.Testable;
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
     * Interleaves the tests execution so to average the disturbing events.
     * Use fractions when the test to be executed are long and
     * should be interleaved more (for usual micro-benchmark 1 should be ok).
     *
     * @param fractions
     *                  How many times each test switch to the next to average
     *                  system's disturbances
     */
    public SingleThreadPerformanceExecutor(final int fractions) {
        this.fractions = fractions;
    }

    /**
     * Executes the given tests for the given number of iterations and
     * return the statistics.
     *
     * @param iterations times a test must be executed
     * @param tests      tests' name and code
     * @return a new instance of {@link PerformanceSample}
     */
    @Override
    public PerformanceSample executeTests(final Map<String, Testable> tests,
            final int[] iterations) {
        final IterationTimeCollector timeCollector =
                new IterationTimeCollector();

        int[] iterationPerFraction =
                calculateIterationPerFraction(fractions, iterations);

        List<IterationData> testData =
                createTestData(tests, iterationPerFraction);

        for (int f = 0; f < fractions; f++) {
            for (IterationData data : testData) {
                data.test.onBeforeSample(data.iteration);

                final long startTime = System.nanoTime();

                for (int t = 0; t < data.iteration; t++) {
                    if (data.test.test() == this) {
                        // forces the return value of test() to be avaluated by
                        // the JVM so that the code will not be evicted by
                        // dead code optimizations.
                        throw new AssertionError();
                    }
                }

                final long elapsed = System.nanoTime() - startTime;
                timeCollector.add(data.name, elapsed, data.iteration);
            }
            if (f + 1 < fractions) {
                // to minimize inter-test noise (at last so order is maintained)
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
            data[index] = new IterationData();
            data[index].name = entry.getKey();
            data[index].test = entry.getValue();
            data[index].iteration = iterationPerFraction[index];
            index++;
        }
        return Arrays.asList(data);
    }

    private static class IterationData {

        String name;
        Testable test;
        int iteration;
    }
}
