package com.fillumina.performance.infrastructure;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class CpuBurner {
    private static volatile int register = timeRelatedRandom();

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
        if (r == 42) {
            register = r;
        }
    }

    // fast way to obtain a pseudo-random value
    private static int timeRelatedRandom() {
        long time = System.nanoTime();
        return (int) (time ^ (time >>> 32));
    }
}
