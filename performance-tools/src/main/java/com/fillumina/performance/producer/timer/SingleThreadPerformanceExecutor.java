package com.fillumina.performance.producer.timer;

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

    private FractionedIterationFactory fractionHolderCreator;

    /**
     * By default the tests will be interleaved 100 times unless the
     * required total iterations per test is less than 1000.
     */
    public SingleThreadPerformanceExecutor() {
        this(new FractionedIterationCalculator(100, 1_000));
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
        this(new FractionedIterationCalculator(fractions,
                maxInterleavedIterations));
    }

    /** Uses a flexible way to define interleaving. */
    public SingleThreadPerformanceExecutor(
            final FractionedIterationFactory fractionHolderCreator) {
        this.fractionHolderCreator = fractionHolderCreator;
    }

    /**
     * Interleave the tests execution so to average the disturbing events.
     *
     * @param iterations times a test must be executed
     * @param tests      tests' name and code
     * @return a new instance of {@link LoopPerformances}
     */
    @Override
    public LoopPerformances executeTests(final long iterations,
            final Map<String, Testable> tests) {
        final RunningLoopPerformances performances =
                new RunningLoopPerformances(iterations);

        final FractionedIteration fractions =
                fractionHolderCreator.createFractionHolder(iterations);
        final long iterationsPerFraction = fractions.iterationsPerFraction;

        for (int f=0; f<fractions.fractionsNumber; f++) {
            for (Map.Entry<String, Testable> entry: tests.entrySet()) {
                final String msg = entry.getKey();
                final Testable testable = entry.getValue();

                testable.beforeTest((int)iterationsPerFraction);

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

    /** The total number of iterations is iterationPerFraction * fractionNumber. */
    public static final class FractionedIteration {
        private final long iterationsPerFraction, fractionsNumber;

        public FractionedIteration(final long iterationsPerFraction,
                final long fractionsNumber) {
            this.iterationsPerFraction = iterationsPerFraction;
            this.fractionsNumber = fractionsNumber;
        }
    }

    /** *  Factory for {@link FractionedIteration}. */
    public interface FractionedIterationFactory {

        /**
         * @param iterations is the required number of iterations to be
         * performed by each test.
         */
        FractionedIteration createFractionHolder(final long iterations);
    }

    /**
     * This is the default implementation of {@link FractionedIterationFactory}
     * that accepts the number of interleaving intervals (<i>fractions</i>)
     * and the minimum iterations number to apply interleaving.
     */
    private static class FractionedIterationCalculator
            implements FractionedIterationFactory {
        private int fractions;
        private int maxInterleavedIterations;

        /**
         * @param fractions
         *          The times each test switch to the next to average
         *          system's disturbances
         * @param maxInterleavedIterations
         *          The number of iterations under which tests are not
         *          interleaved because the iterations per interval would be
         *          to few to be useful.
         */
        public FractionedIterationCalculator(final int fractions,
                final int maxInterleavedIterations) {
            this.fractions = fractions;
            this.maxInterleavedIterations = maxInterleavedIterations;
        }

        @Override
        public FractionedIteration createFractionHolder(final long iterations) {
            if (iterations > maxInterleavedIterations) {
                return new FractionedIteration(fractions, iterations / fractions);
            } else {
                return new FractionedIteration(1, iterations);
            }
        }
    }
}
