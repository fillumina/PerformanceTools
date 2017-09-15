package com.fillumina.performance.util;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class CpuBurner {
    // no problem if this is accessed concurrently or out of order
    private static int register = timeRelatedRandom();

    /** Keeps CPU working for the given number of milliseconds. */
    public static void burnMillis(long millis) {
        long end = System.nanoTime() + millis * 1_000_000;
        do {
            burn(10_000);
        } while (System.nanoTime() < end);
    }

    /**
     * Burns CPU cycles proportionally to the given parameter.
     * The correlation between the given cycles and the time spent by
     * this method is linear.
     *
     * @param cycles
     */
    public static void burn(long cycles) {
        int r = register;
        for (long l=0; l<cycles; l++) {
            r = ((r >>> 1) ^ (-(r & 1) & -536870400));
        }
        if (r == 0) {
            throw new AssertionError("cannot happen!");
        }
        register = r;
    }

    // fast way to obtain a pseudo-random odd non zero value
    private static int timeRelatedRandom() {
        long time = System.nanoTime();
        return 1 | (int) (time ^ (time >>> 32));
    }
}
