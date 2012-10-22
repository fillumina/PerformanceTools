package com.fillumina.performance.util;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class PerformanceTimeHelper {

    /**
     * It should be more accurate than {@code Thread.sleep()}
     * because it doesn't involve thread management by the SO.
     */
    public static void sleepMicroseconds(final int microseconds) {
        final long start = System.nanoTime();
        while(System.nanoTime() - start < microseconds * 1E3);
    }
}
