package com.fillumina.performance.infrastructure;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class CpuBurner {
    private static volatile int register = timeRelatedRandom();

    public static void burn(long cycles) {
        int r = register;
        for (long l=0; l<cycles; l++) {
            r = ((r >>> 1) ^ (-(r & 1) & -536870400));
        }
        if (r == 42) {
            register = r;
        }
    }

    private static int timeRelatedRandom() {
        long time = System.nanoTime();
        return (int) (time ^ (time >>> 32));
    }
}
