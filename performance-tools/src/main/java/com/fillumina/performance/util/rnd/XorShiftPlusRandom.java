package com.fillumina.performance.util.rnd;

import java.util.Random;

/**
 * XorShiftPlus implementation by Sebastiano Vigna.
 *
 * All xorshift generators fail some test out of
 * <a href='https://en.wikipedia.org/wiki/TestU01'>TestU01</a>'s BigCrush test
 * suite.
 * This is true of all generators based on linear recurrences,
 * such as the Mersenne Twister or WELL. This algorithm scramble
 * the output of the normal xorshift generator to improve its quality.
 * <p>
 * Not thread safe.
 * <p>
 * For more see http://maths.uncommons.org/.
 *
 * @see <a href='http://xorshift.di.unimi.it/'>Xorshift</a>
 * @see <a href='https://en.wikipedia.org/wiki/Xorshift'>Wikipedia: Xorshift</a>
 * @author Sebastiano Vigna (C version)
 * @author Francesco Illuminati (JAVA adaptation)
 */
public class XorShiftPlusRandom extends Random {
    private static final long serialVersionUID = 1L;

    private long s0, s1;

    public XorShiftPlusRandom() {
        this(System.nanoTime());
    }

    public XorShiftPlusRandom(long seed) {
        this.s0 = seed;
    }

    @Override
    public long nextLong() {
        long x = s0;
        long y = s1;
        s0 = y;
        x ^= x << 23; // a
        x ^= x >>> 17; // b
        x ^= y ^ (y >>> 26); // c
        s1 = x;
        return x + y;
    }

    @Override
    protected int next(int bits) {
        return (int) (nextLong() >>> (64 - bits));
    }
}
