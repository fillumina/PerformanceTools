package com.fillumina.performance.infrastructure;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class CpuBurner {
    // no problem if this is accessed concurrenlty
    private static int register = timeRelatedRandom();

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
