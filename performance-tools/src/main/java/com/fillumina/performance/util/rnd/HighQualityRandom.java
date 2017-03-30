package com.fillumina.performance.util.rnd;

import java.util.Random;

/**
 * Within the same order of magnitude of the speed of {@link java.util.Random}
 * and generates numbers of a much higher quality.
 * However, it still does not generate numbers of cryptographic quality.
 * Not thread safe.
 *
 * @see <a href='http://www.javamex.com/tutorials/random_numbers/numerical_recipes.shtml'>
 * Numerical Recipes</a>
 */
public class HighQualityRandom extends Random {
    private static final long serialVersionUID = 1L;

    private long u;
    private long v = 4101842887655102017L;
    private long w = 1;

    public HighQualityRandom() {
        this(System.nanoTime());
    }

    public HighQualityRandom(long seed) {
        u = seed ^ v;
        nextLong();
        v = u;
        nextLong();
        w = v;
        nextLong();
    }

    @Override
    public final long nextLong() {
        u = u * 2862933555777941757L + 7046029254386353087L;
        v ^= v >>> 17;
        v ^= v << 31;
        v ^= v >>> 8;
        w = 4294957665L * (w & 0xffffffff) + (w >>> 32);
        long x = u ^ (u << 21);
        x ^= x >>> 35;
        x ^= x << 4;
        long ret = (x + v) ^ w;
        return ret;
    }

    @Override
    protected int next(int bits) {
        return (int) (nextLong() >>> (64 - bits));
    }

}
