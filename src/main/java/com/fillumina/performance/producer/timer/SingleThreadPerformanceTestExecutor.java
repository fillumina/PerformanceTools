package com.fillumina.performance.producer.timer;

import java.io.Serializable;
import java.util.Map;

/**
 * This {@link PerformanceTestExecutor} use a single thread and interleaves
 * the test executions so to average the effect of a disturbance in the
 * performances offered by the system.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SingleThreadPerformanceTestExecutor
        implements PerformanceTestExecutor, Serializable {
    private static final long serialVersionUID = 1L;

    private FractionHolderCreator fractionHolderCreator;

    public SingleThreadPerformanceTestExecutor() {
        this(new FractionCalculator(100, 1_000));
    }

    public SingleThreadPerformanceTestExecutor(
            final FractionHolderCreator fractionHolderCreator) {
        this.fractionHolderCreator = fractionHolderCreator;
    }

    /**
     * Interleave the tests execution so to average the disturbing events.
     */
    @Override
    public LoopPerformances executeTests(final int iterations,
            final Map<String, Runnable> executors) {
        final RunningLoopPerformances performances =
                new RunningLoopPerformances(iterations);

        final FractionHolder fractions =
                fractionHolderCreator.createFractionHolder(iterations);

        for (int f=0; f<fractions.fractionsNumber; f++) {
            for (Map.Entry<String, Runnable> entry: executors.entrySet()) {
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

    public static class FractionHolder {
        final long iterationsPerFraction, fractionsNumber;

        public FractionHolder(final long iterationsPerFraction,
                final long fractionsNumber) {
            this.iterationsPerFraction = iterationsPerFraction;
            this.fractionsNumber = fractionsNumber;
        }

    }

    public interface FractionHolderCreator {
        FractionHolder createFractionHolder(final int iterations);
    }

    public static class FractionCalculator implements FractionHolderCreator {
        public int fractions;
        public int maxInterleavedIterations;

        public FractionCalculator(final int fractions,
                final int maxInterleavedIterations) {
            this.fractions = fractions;
            this.maxInterleavedIterations = maxInterleavedIterations;
        }

        @Override
        public FractionHolder createFractionHolder(final int iterations) {
            if (iterations > maxInterleavedIterations) {
                return new FractionHolder(fractions, iterations / fractions);
            } else {
                return new FractionHolder(1, iterations);
            }
        }
    }
}
