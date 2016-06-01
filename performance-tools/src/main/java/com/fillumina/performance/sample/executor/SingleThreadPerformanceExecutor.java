package com.fillumina.performance.sample.executor;

import com.fillumina.performance.sample.IterationTimeCollector;
import com.fillumina.performance.sample.PerformanceSample;
import com.fillumina.performance.sample.Testable;
import java.io.Serializable;
import java.util.ArrayList;
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
     *          How many times each test switch to the next to average
     *          system's disturbances
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
            final int iterations) {
        final IterationTimeCollector timeCollector =
                new IterationTimeCollector();

        int iterationsPerFraction = iterations / fractions;
        int fractionsNumber = fractions;

        // check for too few iterations
        if (iterationsPerFraction == 0) {
            iterationsPerFraction = iterations;
            fractionsNumber = 1;
        }

        List<Map.Entry<String,Testable>> testList =
                new ArrayList<>(tests.entrySet());

        for (int f=0; f<fractionsNumber; f++) {
            for (Map.Entry<String, Testable> entry: testList) {
                final String msg = entry.getKey();
                final Testable testable = entry.getValue();

                testable.onBeforeSample(iterationsPerFraction);

                final long startTime = System.nanoTime();

                for (int t=0; t<iterationsPerFraction; t++) {
                    if (testable.test() == this) {
                        // forces the return value of test() to be avaluated by
                        // the JVM so that the code will not be evicted by
                        // dead code optimizations.
                        throw new AssertionError();
                    }
                }

                final long elapsed = System.nanoTime() - startTime;
                timeCollector.add(msg, elapsed, iterationsPerFraction);
            }
            // to minimize inter-test noise (at last so order is maintained)
            Collections.shuffle(testList);
        }
        return timeCollector.createPerformanceSample();
    }
}
