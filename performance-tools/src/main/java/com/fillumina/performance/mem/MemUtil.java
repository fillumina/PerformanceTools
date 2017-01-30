package com.fillumina.performance.mem;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MemUtil {

    /**
     * Align values to be multiple of a given power of 2.
     *
     * @param value the number to align
     * @param step  the number value should be divisible by (must be a power of 2)
     * @return the closest number greater than value and divisible by step
     */
    public static long align(long value, int step) {
        if ((~step & (step - 1)) != (step - 1)) {
            throw new IllegalArgumentException("step is not a power of 2: " +
                    step);
        }
        return (value + (step - 1)) & ~(step - 1);
    }

}
