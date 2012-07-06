package com.fillumina.performance.producer.timer;

import java.io.Serializable;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SingleThreadPerformanceExecutor
        implements PerformanceExecutor, Serializable {
    private static final long serialVersionUID = 1L;

    public static final int FRACTIONS = 100;

    /**
     * Interleave the tests execution so to average the disturbing events.
     */
    @Override
    public void executeTests(final int times,
            final Map<String, Runnable> executors,
            final RunningPerformances timeMap) {
        long fraction = times / FRACTIONS;
        fraction = fraction > 100 ? fraction : 1;

        for (int f=0; f<FRACTIONS; f++) {
            for (Map.Entry<String, Runnable> entry: executors.entrySet()) {
                final String msg = entry.getKey();
                final Runnable runnable = entry.getValue();

                final long time = System.nanoTime();

                for (int t=0; t<fraction; t++) {
                    runnable.run();
                }

                timeMap.add(msg, System.nanoTime() - time);
            }
        }
    }

}
