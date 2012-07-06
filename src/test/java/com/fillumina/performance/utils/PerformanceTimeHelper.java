package com.fillumina.performance.utils;

/**
 *
 * @author fra
 */
public class PerformanceTimeHelper {

    /**
     * It should be more accurate than {@code Thread.sleep()}
     * because it doesn't involve SO thread management.
     */
    public static void sleepMicroseconds(final int microseconds) {
        final long start = System.nanoTime();
        while(System.nanoTime() - start < microseconds * 1E3);
    }

}
