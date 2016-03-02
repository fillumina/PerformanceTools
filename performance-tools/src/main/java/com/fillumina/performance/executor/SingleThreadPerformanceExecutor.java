package com.fillumina.performance.executor;

import com.fillumina.performance.producer.LoopPerformances;
import com.fillumina.performance.producer.RunningLoopPerformances;
import java.io.Serializable;
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
    private final int maxInterleavedIterations;

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
     * @param maxInterleavedIterations
     *          The number of iterations under which tests are not
     *          interleaved because the iterations per interval would be
     *          too few to be useful.
     */
    public SingleThreadPerformanceExecutor(final int fractions,
            final int maxInterleavedIterations) {
        this.fractions = fractions;
        this.maxInterleavedIterations = maxInterleavedIterations;
    }

    /**
     * Interleave the tests execution so to average the disturbing events.
     *
     * @param iterations times a test must be executed
     * @param tests      tests' name and code
     * @return a new instance of {@link LoopPerformances}
     */
    @Override
    public LoopPerformances executeTests(final Map<String, Testable> tests,
            final int iterations) {
        final RunningLoopPerformances performances =
                new RunningLoopPerformances(iterations);

        int iterationsPerFraction;
        int fractionsNumber;
        if (iterations > maxInterleavedIterations) {
            fractionsNumber = fractions;
            iterationsPerFraction = (int)(iterations / fractions);
        } else {
            fractionsNumber = 1;
            iterationsPerFraction = iterations;
        }

        for (int f=0; f<fractionsNumber; f++) {
            for (Map.Entry<String, Testable> entry: tests.entrySet()) {
                final String msg = entry.getKey();
                final Testable testable = entry.getValue();

                testable.beforeTest(iterationsPerFraction);

                final long time = System.nanoTime();

                for (int t=0; t<iterationsPerFraction; t++) {
                    if (testable.test() == this) {
                        // forces the return value of test() to be avaluated by
                        // the JVM so that the code will not be evicted by
                        // dead code optimizations.
                        throw new AssertionError();
                    }
                }

                performances.add(msg, System.nanoTime() - time);
            }
        }
        return performances.getLoopPerformances();
    }
}
