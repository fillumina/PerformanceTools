package com.fillumina.performance.util;

/**
 *
 * @author Francesco Illuminati
 */
public class PerformanceTimeHelper {

    /**
     * It should be more accurate than {@code Thread.sleep()}
     * because it doesn't involve thread management by the SO.
     */
    public static void sleepMicroseconds(final int microseconds) {
        final long start = System.nanoTime();
        final long ns = microseconds * 1_000L;
        while(System.nanoTime() - start < ns) {}
    }
}
