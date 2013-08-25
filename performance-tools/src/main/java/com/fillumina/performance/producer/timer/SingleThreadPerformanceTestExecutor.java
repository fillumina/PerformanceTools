package com.fillumina.performance.producer.timer;

import com.fillumina.performance.producer.RunningLoopPerformances;
import com.fillumina.performance.producer.LoopPerformances;
import java.io.Serializable;
import java.util.Map;

/**
 * This {@link PerformanceTestExecutor} uses a single thread and interleaves
 * the test executions so to average the effect of a disturbance in the
 * performances offered by the system.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SingleThreadPerformanceTestExecutor
        implements PerformanceTestExecutor, Serializable {
    private static final long serialVersionUID = 1L;

    private FractionHolderCreator fractionHolderCreator;

    /**
     * By default the tests will be interleaved 100 times unless the
     * required total iterations per test is less than 1000.
     */
    public SingleThreadPerformanceTestExecutor() {
        this(new FractionCalculator(100, 1_000));
    }

    /**
     * @param fractions
     *          The times each test switch to the next to average
     *          system's disturbances
     * @param maxInterleavedIterations
     *          The number of iterations under which tests are not
     *          interleaved because the iterations per interval would be
     *          to few to be useful.
     */
    public SingleThreadPerformanceTestExecutor(final int fractions,
            final int maxInterleavedIterations) {
        this(new FractionCalculator(fractions, maxInterleavedIterations));
    }

    public SingleThreadPerformanceTestExecutor(
            final FractionHolderCreator fractionHolderCreator) {
        this.fractionHolderCreator = fractionHolderCreator;
    }

    /**
     * Interleave the tests execution so to average the disturbing events.
     */
    @Override
    public LoopPerformances executeTests(final long iterations,
            final Map<String, Runnable> tests) {
        final RunningLoopPerformances performances =
                new RunningLoopPerformances(iterations);

        final FractionHolder fractions =
                fractionHolderCreator.createFractionHolder(iterations);

        for (int f=0; f<fractions.fractionsNumber; f++) {
            for (Map.Entry<String, Runnable> entry: tests.entrySet()) {
                final String msg = entry.getKey();
                final Runnable runnable = entry.getValue();

                final long time = System.nanoTime();

                for (int t=0; t<fractions.iterationsPerFraction; t++) {
                    runnable.run();
                }

                performances.add(msg, System.nanoTime() - time);
            }
        }
        return performances.getLoopPerformances();
    }

    public static final class FractionHolder {
        private final long iterationsPerFraction, fractionsNumber;

        public FractionHolder(final long iterationsPerFraction,
                final long fractionsNumber) {
            this.iterationsPerFraction = iterationsPerFraction;
            this.fractionsNumber = fractionsNumber;
        }
    }

    public interface FractionHolderCreator {
        FractionHolder createFractionHolder(final long iterations);
    }

    /**
     * This is the default implementation of {@link FractionHolderCreator}
     * that accepts the number of interleaving intervals (<i>fractions</i>)
     * and the minimum iterations number to apply interleaving.
     */
    private static class FractionCalculator implements FractionHolderCreator {
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
        public FractionCalculator(final int fractions,
                final int maxInterleavedIterations) {
            this.fractions = fractions;
            this.maxInterleavedIterations = maxInterleavedIterations;
        }

        @Override
        public FractionHolder createFractionHolder(final long iterations) {
            if (iterations > maxInterleavedIterations) {
                return new FractionHolder(fractions, iterations / fractions);
            } else {
                return new FractionHolder(1, iterations);
            }
        }
    }
}
