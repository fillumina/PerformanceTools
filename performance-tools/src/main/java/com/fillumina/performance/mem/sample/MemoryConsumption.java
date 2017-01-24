package com.fillumina.performance.mem.sample;

import com.fillumina.performance.util.MostUsedValueBag;
import java.util.List;

/**
 * Calculates the memory used by some code. This class is <b>NOT</b> thread
 * safe.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
final class MemoryConsumption {

    public static final MemoryConsumption INSTANCE = new MemoryConsumption();

    private static final int SAMPLES = 33;

    private final Runtime rt;
    private final int minAllocableMemory;    // should be 16
    private final int fillerSize = 1 << 24;
    private final long alignment;
    private final long zero;
    private final long dryZero;
    private final String constructionLog;
    private Object[] filler;

    private long startMemory;
    private int start;
    private long usedMem, before;
    private int i, k;

    MemoryConsumption() {
        rt = Runtime.getRuntime();
        StringBuilder buf = new StringBuilder();

        // calculate minimum allocable memory value
        MostUsedValueBag<Integer> minAllocableBag = new MostUsedValueBag<>(10);
        for (k=0; k<10; k++) {
            start();
            minAllocableBag.add(calculateMinimumAllocableMemory());
        }
        minAllocableMemory = minAllocableBag.getMostUsedValue();
        log(buf, minAllocableBag, "minAllocableMemory: ", minAllocableMemory);


        // find static error (zero)
        MostUsedValueBag<Long> zeroBag = new MostUsedValueBag<>(SAMPLES);
        for (k=0; k<SAMPLES; k++) {
            start();
            zeroBag.add(usedMemory());
        }
        dryZero = zeroBag.getMostUsedValue();
        zero = armonize(dryZero, minAllocableMemory);
        log(buf, zeroBag, "zero: ", zero);

        // find memory alignment
        Object[] alignmentArray = new Object[SAMPLES];
        start();
        for (k = 0; k<SAMPLES; k++) {
            alignmentArray[k] = new byte[1]; // 16 + 8 = 24 bytes
        }
        alignment = armonize(
                ((usedMemory() - zero) / SAMPLES) - minAllocableMemory, 8);

        constructionLog = buf.toString();
    }

    private void log(StringBuilder buf,
            Object bag,
            final String message,
            final long value) {
        buf.append(" ")
                .append(message)
                .append(bag.toString())
                .append(" -> ")
                .append(value)
                .append(System.lineSeparator());
    }

    /**
     * Calculates the minimum memory allocable. The smallest object allocable
     * is the empty array ({@code int[0]}).
     */
    private synchronized int calculateMinimumAllocableMemory() {
        start();
        before = rt.totalMemory() - rt.freeMemory();
        for (i=start; i<filler.length; i++) {
            filler[i] = new int[0]; // 16 bytes
            usedMem = rt.totalMemory() - rt.freeMemory() - before;
            if (usedMem > 0) {
                return (int) (usedMem / (i - start));
            }
        }
        throw new AssertionError("granularity error");
    }

    /** Call this method before the code to analyze. */
    public synchronized final void start() {
        filler = new Object[fillerSize];
        start = 0;

        // allocates a lot of memory to force GC
        int size = (fillerSize > 0) ? 1 << 21 : 0;
        byte[][] array = new byte[8][];
        for (int i=0; i<array.length; i++) {
            array[i] = new byte[size];
            array[i][0] = 66;
        }
        if (fillerSize > 0) {
            array = null;
        }
        System.gc();
        try {
            Thread.sleep(250);
        } catch (InterruptedException e) {
            // gives time to the JVM to perform a GC
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
                return usedMem - ((i - start) * minAllocableMemory);
            } else if (usedMem < 0) {
                // gc happend
                // System.out.println("GC occurred, returning " + Long.MIN_VALUE);
                return Long.MIN_VALUE; // so it is filtered out as an outlier
            }
        }
        // too much memory used?
        // TODO check for too much memory used > 16M * 16 B
        throw new AssertionError("used memory assessment failed: " +
                toString());
    }

    /** Minimum amount of allocable memory (16). */
    public int getMinimalAllocableMemory() {
        return minAllocableMemory;
    }

    /** Minimum memory allocable without padding. */
    public long getAlignment() {
        return alignment;
    }

    @Override
    public String toString() {
        final String nl = System.lineSeparator();
        return getClass().getSimpleName() + " debug info:" + nl +
                constructionLog +
//                " minAllocableMem: " + minAllocableMemory + nl +
                " dryZero:     " + dryZero + nl +
                " alignment:   " + alignment + nl +
                " before:      " + before + nl +
                " intialMem:   " + start + nl +
                " filler size: " + filler.length + nl +
                " idx:         " + i + nl +
                " usedMem:     " + usedMem + nl;
    }

    static long armonize(long z, long step) {
        return (long) Math.floor(z * 1.0 / step) * step;
    }

    static long getUpperValue(List<Long> list) {
        long upper = Long.MIN_VALUE;
        for (long v : list) {
            if (v > upper) {
                upper = v;
            }
        }
        return upper;
    }
}
