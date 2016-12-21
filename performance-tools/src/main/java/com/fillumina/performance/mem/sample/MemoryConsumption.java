package com.fillumina.performance.mem.sample;

import com.fillumina.performance.util.MostUsedValueBag;

/**
 * Calculates the memory used by some code.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
final class MemoryConsumption {

    public static final MemoryConsumption INSTANCE = new MemoryConsumption();

    private final Runtime rt;
    private final int byteGranularity;       // should be 16
    private final int fillerSize = 1 << 24;  // 16 M
    private final long zero;
    private final long minPadding;
    private Object[] filler;

    private long startMemory;
    private int start;
    private long usedMem, before;
    private int i, k;

    MemoryConsumption() {
        rt = Runtime.getRuntime();

        // calculate granularity
        MostUsedValueBag<Integer> bag = new MostUsedValueBag<>(10);
        for (k=0; k<10; k++) {
            start();
            bag.add(calculateGranularity());
        }
        byteGranularity = bag.getMostUsedValue();

        zero = calculateZero();
        minPadding = calculateMinPadding() - byteGranularity;
        System.out.println(this);
    }

    private static final int ZERO_SAMPLES = 30;
    private long calculateZero() {
        MostUsedValueBag<Long> bag = new MostUsedValueBag<>(ZERO_SAMPLES);
        for (k=0; k<ZERO_SAMPLES; k++) {
            start();
            bag.add(usedMemory());
        }
        return bag.getMostUsedValue();
    }

    /**
     * Calculates the minimum memory allocable. The smallest object allocable
     * is the empty array ({@code int[0]}).
     */
    private synchronized int calculateGranularity() {
        start();
        before = rt.totalMemory() - rt.freeMemory();
        for (i=start; i<filler.length; i++) {
            filler[i] = new int[0]; // 16 bytes
            usedMem = rt.totalMemory() - rt.freeMemory() - before;
            if (usedMem > 0) {
                // granularity is the size of new int[0]
                int granularity =
                        (int) Math.floor(usedMem * 1.0 / (i - start));
                return granularity;
            }
        }
        throw new AssertionError("granularity error");
    }

    private long calculateMinPadding() {
        Object[] b = new Object[byteGranularity];
        start();
        for (k = 0; k<byteGranularity; k++) {
            b[k] = new byte[1]; // 16 + 8 = 24 bytes
        }
        return getUsedMemory() / byteGranularity;
    }

    /** Call this method before the code to analyze. */
    public synchronized final void start() {
        filler = new Object[fillerSize];
        start = 0;
        System.gc();
        try {
            Thread.sleep(150);
        } catch (InterruptedException e) {
            // helps jvm to perform a gc
        }

        before = rt.totalMemory() - rt.freeMemory();
        for (i=0; i<filler.length; i++) {
            filler[i] = new int[0];
            startMemory = rt.totalMemory() - rt.freeMemory();
            if (startMemory > before) {
                start = (i == 0) ? 0 : i + 1;
                return;
            } else if (startMemory < before) {
                throw new AssertionError("GC occurred");
            }
        }
        throw new AssertionError("first threshold not reached");
    }

    /**
     * Call this method after the code to analyze.
     * Remember that if a garbage collection takes place while testing
     * the result of this test will be wrong. Always take several samples
     * so to be able to exclude outliers.
     *
     * @return the byte used by the code.
     */
    public synchronized final long getUsedMemory() {
        return usedMemory() - zero;
    }

    private long usedMemory() {
        for (i=start; i<filler.length; i++) {
            filler[i] = new int[0]; // 16 bytes
            usedMem = rt.totalMemory() - rt.freeMemory() - startMemory;
            if (usedMem > 0) {
                return usedMem - ((i - start) * byteGranularity);
            } else if (usedMem < 0) {
                // gc happend
                // TODO could use a counter of GC so to reset everything?
                System.out.println("GC occurred, returning " + Long.MIN_VALUE);
                return Long.MIN_VALUE; // so it is filtered out as an outlier
            }
        }
        // too much memory used?
        // TODO check for too much memory used > 16M * 16 B
        throw new AssertionError("used memory assessment failed: " +
                toString());
    }

    /** Minimum amount of allocable memory (16). */
    public int getByteGranularity() {
        return byteGranularity;
    }

    /** Memory used for function calling and overheads. */
    public long getZero() {
        return zero;
    }

    /** Minimum memory allocable without padding. */
    public long getMinPadding() {
        return minPadding;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "byteGranularity=" + byteGranularity +
                ", zero=" + zero +
                ", padding=" + minPadding +
                ", intialMem=" + start +
                ", usedMem=" + usedMem +
                ", filler_size=" + filler.length +
                ", idx=" + i + '}';
    }
}
