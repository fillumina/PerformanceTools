package com.fillumina.performance.util.rnd;

import java.util.Random;

/**
 * Useful to provide fast random numbers in a loop. It's enough to avoid
 * prediction optimization and it's robustness depends on the given random
 * generator. Of course it's a very very poor random number generator of its
 * own because of the loop.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class CachedRandom extends Random {

    private static final long serialVersionUID = 1L;

    private final long[] array;
    private final int mask;
    private int counter = -1;

    public CachedRandom() {
        this(1 << 16);
    }

    public CachedRandom(int cacheSize) {
        this(cacheSize, new HighQualityRandom());
    }

    public CachedRandom(int cacheSize, Random rnd) {
        int size = roundUpToPowerOf2(cacheSize);
        this.array = new long[size];
        for (int i=0; i<size; i++) {
            array[i] = rnd.nextLong();
        }
        this.mask = size - 1;
    }

    private static int roundUpToPowerOf2(int number) {
        // assert number >= 0 : "number must be non-negative";
        return (number > 1) ? Integer.highestOneBit((number - 1) << 1) : 2; //1;
    }

    @Override
    public final long nextLong() {
        int c = (counter + 1) & mask;
        long result = array[c];
        counter = c;
        return result;
    }

    @Override
    protected int next(int bits) {
        return (int) (nextLong() >>> (64 - bits));
    }
}
