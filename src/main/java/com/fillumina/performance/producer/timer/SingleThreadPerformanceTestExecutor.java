package com.fillumina.performance.producer.timer;

import java.io.Serializable;
import java.util.Map;

/**
 * This {@link PerformanceTestExecutor} use a single thread and interleaves
 * the tests execution so to average the effect of a disturbance in the
 * performances offered by the system.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SingleThreadPerformanceTestExecutor
        implements PerformanceTestExecutor, Serializable {
    private static final long serialVersionUID = 1L;

    public static final int FRACTIONS = 100;
    public static final int LIMIT_INTERLEAVED_ITERATIONS = FRACTIONS * 10;

    /**
     * Interleave the tests execution so to average the disturbing events.
     */
    @Override
    public LoopPerformances executeTests(final int iterations,
            final Map<String, Runnable> executors) {
        final RunningLoopPerformances performances =
                new RunningLoopPerformances(iterations);

        final Fractions fractions = new Fractions(iterations);

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

    private static class Fractions {
        final long iterationsPerFraction, fractionsNumber;

        public Fractions(final int iterations) {
            if (iterations > LIMIT_INTERLEAVED_ITERATIONS) {
                fractionsNumber = FRACTIONS;
                iterationsPerFraction = iterations / FRACTIONS;
            } else {
                fractionsNumber = 1;
                iterationsPerFraction = iterations;
            }
        }
    }
}
