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

    /**
     * Interleave the tests execution so to average the disturbing events.
     */
    @Override
    public LoopPerformances executeTests(final int iterations,
            final Map<String, Runnable> executors) {
        final RunningLoopPerformances performances =
                new RunningLoopPerformances(iterations);
        long fraction = iterations / FRACTIONS;
        fraction = fraction > 100 ? fraction : 1;

        for (int f=0; f<FRACTIONS; f++) {
            for (Map.Entry<String, Runnable> entry: executors.entrySet()) {
                final String msg = entry.getKey();
                final Runnable runnable = entry.getValue();

                final long time = System.nanoTime();

                for (int t=0; t<fraction; t++) {
                    runnable.run();
                }

                performances.add(msg, System.nanoTime() - time);
            }
        }
        return performances.getLoopPerformances();
    }

}
