package com.fillumina.performance.mem;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MemUtil {

    /**
     * Return the next multiple of a given power of 2.
     *
     * @param x the number to alignUp
     * @param step  the number value should be divisible by (must be a power of 2)
     * @return the closest number greater than value and divisible by step
     */
    public static long alignUp(long x, long step) {
        if (!isPowerOfTwo(step)) {
            throw new IllegalArgumentException("step is not a power of 2: " +
                    step);
        }
        return (x + (step - 1)) & ~(step - 1);
    }

    /**
     * Return the previous multiple of a given power of 2.
     *
     * @param x the number to alignUp
     * @param step  the number value should be divisible by (must be a power of 2)
     * @return the closest number greater than value and divisible by step
     */
    public static long alignDown(long x, long step) {
        if (!isPowerOfTwo(step)) {
            throw new IllegalArgumentException("step is not a power of 2: " +
                    step);
        }
        return x & ~(step - 1);
    }

    /**
     * @return {@code true} if x is a positive power of 2 (0 included)
     */
    public static boolean isPowerOfTwo(long x) {
        return (x != 1) && (~x & (x - 1)) == (x - 1);
    }

}
