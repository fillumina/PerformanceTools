package com.fillumina.performance.util;

/**
 * Produces pseudo-random bits.
 *
 * @see https://en.wikipedia.org/wiki/Linear_feedback_shift_register
 * @see https://community.oracle.com/thread/1661705?start=0&tstart=0
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class LinearFeedbackShiftRegister {

    private final int taps;
    private final int mask;
    private int register = 1;

    /** Produces a sequence of length 2^32 */
    public LinearFeedbackShiftRegister() {
        // maximum length = 2^32
        this(32, 32, 31, 29, 1);
    }

    /**
     *
     * @param bit  set the mask to n bit
     * @param taps set the taps
     */
    public LinearFeedbackShiftRegister(int bit, int... taps) {
        this.taps = makeTaps(taps);
        this.mask = makeMask(bit);
    }

    public int next() {
        register = ((register >>> 1) ^ (-(register & 1) & taps)) & mask;
        return register;
    }

    /**
     * Generates a mask that will preserve the n low order bits and put 0's into
     * the high order bits.
     */
    private static int makeMask(int n) {
        int m = 0;
        for (int i = 0; i < n; i++) {
            m |= 1 << i;
        }
        return m;
    }

    private static int makeTaps(int... array) {
        int taps = 0;
        for (int a : array) {
            taps |= (1 << (a - 1));
        }
        return taps;
    }
}
