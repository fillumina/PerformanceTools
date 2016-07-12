package com.fillumina.performance.util.formatter;

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
        final long end = System.nanoTime() + microseconds * 1_000L;
        while(System.nanoTime() < end) {}
    }
}
