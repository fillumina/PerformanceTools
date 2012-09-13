package com.fillumina.performance.producer.timer;

import com.fillumina.performance.producer.RunningLoopPerformances;
import com.fillumina.performance.producer.LoopPerformances;
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

    public static class FractionHolder {
        final long iterationsPerFraction, fractionsNumber;

        public FractionHolder(final long iterationsPerFraction,
                final long fractionsNumber) {
            this.iterationsPerFraction = iterationsPerFraction;
            this.fractionsNumber = fractionsNumber;
        }

    }

    public interface FractionHolderCreator {
        FractionHolder createFractionHolder(final long iterations);
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
        public FractionHolder createFractionHolder(final long iterations) {
            if (iterations > maxInterleavedIterations) {
                return new FractionHolder(fractions, iterations / fractions);
            } else {
                return new FractionHolder(1, iterations);
            }
        }
    }
}
