package com.fillumina.performance.sample.executor;

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
    private final int minIteractionPerFraction;

    /**
     * By default the tests will be interleaved 100 times unless the
     * required total iterations per test is less than 1000.
     */
    public SingleThreadPerformanceExecutor() {
        this(100, 1_000);
    }

    /**
     * @param fractions
     *          How many times each test switch to the next to average
     *          system's disturbances
     * @param minIteractionPerFraction
     *          The number of iterations under which tests are not
     *          interleaved because the iterations per interval would be
     *          too few to be useful.
     */
    public SingleThreadPerformanceExecutor(final int fractions,
            final int minIteractionPerFraction) {
        this.fractions = fractions;
        this.minIteractionPerFraction = minIteractionPerFraction;
    }

    /**
     * Interleave the tests execution so to average the disturbing events.
     *
     * @param iterations times a test must be executed
     * @param tests      tests' name and code
     * @return a new instance of {@link PerformanceSample}
     */
    @Override
    public PerformanceSample executeTests(final Map<String, Testable> tests,
            final int iterations) {
        final PerformanceSample performances =
                new PerformanceSample();

        int iterationsPerFraction = iterations / fractions;
        int fractionsNumber;

        if (iterationsPerFraction >= minIteractionPerFraction) {
            fractionsNumber = fractions;
        } else {
            fractionsNumber = 1;
            iterationsPerFraction = iterations;
        }

        List<Map.Entry<String,Testable>> testList =
                new ArrayList<>(tests.entrySet());

        for (int f=0; f<fractionsNumber; f++) {
            for (Map.Entry<String, Testable> entry: testList) {
                final String msg = entry.getKey();
                final Testable testable = entry.getValue();

                testable.onBeforeSample(iterationsPerFraction);

                final long time = System.nanoTime();

                for (int t=0; t<iterationsPerFraction; t++) {
                    if (testable.test() == this) {
                        // forces the return value of test() to be avaluated by
                        // the JVM so that the code will not be evicted by
                        // dead code optimizations.
                        throw new AssertionError();
                    }
                }

                final long elapsed = System.nanoTime() - time;
                performances.add(msg, elapsed, iterationsPerFraction);
            }
            // to minimize inter-test noise (at last so order is maintained)
            Collections.shuffle(testList);
        }
        return performances;
    }
}
