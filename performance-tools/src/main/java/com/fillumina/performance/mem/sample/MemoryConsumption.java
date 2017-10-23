package com.fillumina.performance.mem.sample;

import com.fillumina.performance.mem.MemUtil;
import com.fillumina.performance.util.MostUsedValueBag;

/**
 * Estimates the used memory.
 * JVM doesn't report used memory directly, instead it must be calculated
 * by the formula {@link Runtime#totalMemory()} - {@link Runtime#freeMemory()}
 * which has major problems:
 * <ol>
 * <li>Its working depends on the JDK and the memory management implementation;
 * <li>It reports its values without great accuracy (rounded to about 1 MiB);
 * <li>The accuracy of the reported values changes with the amount of memory used
 * (it becomes very unstable and misleading around 256 KiB of used memory);
 * <li>Occasionally returned values might be completely wrong (depending on
 * JDK internals or because a GC has been executed during testing).
 * </ol>
 * Although all these limitations this class employs a hack to report quite
 * accurate results at least until about 256 KiB of used/allocated memory.
 * <p>
 * The only way to have reliable results is to repeat the estimations many times
 * and evaluate the results carefully.
 * <p>
 * This class is <b>NOT</b> thread safe. You must particularly <b>avoid to run
 * more than one memory test at a time</b>.
 * <p
 * Because the mechanism used in this class is very 'hacky' it could change
 * in next versions of the code. Don't use this class directly.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
final class MemoryConsumption {

    public static final MemoryConsumption INSTANCE = new MemoryConsumption();

    private static final int SAMPLES = 33;

    private final Runtime rt;

    /** Min memory allocable, usually an empty array as {@code new int[0]}. */
    private final int minAllocableMemory;

    /** Size of the filler array used to consume memory. */
    private final int fillerSize = 1 << 24;

    /** New memory is allocated in chunks of this size. */
    private final long alignment;

    /** Static aligned error that must be subtracted to measure. */
    private final long zero;

    /** Static unaligned error. */
    private final long dryZero;

    /** Size of the chunk in which memory usage is reported by JVM. */
    private final long chunkSize;

    /** Log actions leading to building this class. */
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
        chunkSize = i * minAllocableMemory;
        dryZero = zeroBag.getMostUsedValue();
        zero = MemUtil.alignDown(dryZero, minAllocableMemory);
        log(buf, zeroBag, "zero: ", zero);

        // find memory alignment
        Object[] alignmentArray = new Object[SAMPLES];
        start();
        for (k = 0; k<SAMPLES; k++) {
            alignmentArray[k] = new byte[1]; // 16 + 8 = 24 bytes
        }
        alignment = MemUtil.alignUp(
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
        try {
            filler = new Object[fillerSize];
        } catch (OutOfMemoryError e) {
            throw new RuntimeException("filler size = " + fillerSize, e);
        }
        start = 0;

        // allocates a lot of memory to force GC
        int size = (fillerSize > 0) ? 1 << 21 : 0;
        byte[] array = new byte[size];
        array[0] = 66;
//        byte[][] array = new byte[8][];
//        for (int j=0; j<array.length; j++) {
//            array[j] = new byte[size];
//            array[j][0] = 66;
//        }
        //if (fillerSize > 0) {
            array = null;
        //}
        System.gc();
        try {
            Thread.sleep(50);
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
        long result = usedMemory() - zero;
        filler = null;
        return result;
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
                " chunk:       " + chunkSize + nl +
                " before:      " + before + nl +
                " intialMem:   " + start + nl +
                " filler size: " + fillerSize + nl +
                " idx:         " + i + nl +
                " usedMem:     " + usedMem + nl;
    }
}
