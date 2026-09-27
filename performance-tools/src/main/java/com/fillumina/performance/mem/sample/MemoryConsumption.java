package com.fillumina.performance.mem.sample;

import com.fillumina.performance.mem.MemUtil;
import com.fillumina.performance.util.collection.MostUsedValueBag;

/**
 * Estimates memory usage.
 * JVM doesn't report used memory directly, instead it must be inferred
 * by the formula {@link Runtime#totalMemory()} - {@link Runtime#freeMemory()}
 * which has some major problems:
 * <ol>
 * <li>Its working depends on JDK and memory management implementation;
 * <li>It has an accuracy of about 1 MiB;
 * <li>The accuracy of the reported values changes with the amount of memory used;
 * <li>Occasionally returned values might be completely wrong (depending on
 * JDK internals or if a GC has been executed during testing).
 * </ol>
 * This class employs some tricks to report quite
 * accurate results at least until about 256 KiB of used/allocated memory.
 * <p>
 * The only way to have reliable results is to repeat the estimations many times
 * and evaluate the results carefully.
 * <p>
 * This class is <b>NOT</b> thread safe. You must particularly <b>avoid to run
 * more than one memory test at a time on the same JVM</b>.
 * <p>
 * Because the mechanism used in this class is very 'hacky' it could change
 * in next versions of the code.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
// TODO try with different GC:
// -XX:+UseSerialGC
// -XX:+UseParallelGC
// -XX:+UseConcMarkSweepGC
// -XX:+UseG1GC
final class MemoryConsumption {

    public static final MemoryConsumption INSTANCE = new MemoryConsumption();

    private static final int SAMPLES = 33;

    /**
     * Returned when a garbage collection makes a measurement unusable. It must be
     * discarded before any arithmetic is done on it: subtracting from
     * {@link Long#MIN_VALUE} overflows into a large positive value that then
     * silently poisons averages and comparisons.
     */
    public static final long GC_OCCURRED = Long.MIN_VALUE;

    private final Runtime rt;

    /**
     * Min memory allocable, usually an empty array as {@code new int[0]} or
     * the size of {@code new Object()} .
     */
    private final int minAllocableMemory;

    /** Size of the filler array used to consume memory. */
    private final int fillerSize = 1 << 24;

    /** New memory is allocated in chunks of this size. */
    private final long alignment;

    /** Systematic aligned error that must be subtracted to a measure. */
    private final long zero;

    /** Systematic unaligned error. */
    private final long dryZero;

    /** Size of the chunk in which memory usage is reported by JVM. */
    private final long chunkSize;

    /** Log actions leading to building this class. */
    private final String constructionLog;

    // variables used internally
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

        // find systematic error (zero)
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
        if (alignmentArray[0] == null) {
            throw new AssertionError();
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
        for (i=start; i<filler.length; i++) {
            filler[i] = new int[0]; // 16 bytes
            usedMem = rt.totalMemory() - rt.freeMemory() - startMemory;
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
            // minimal allocable memory
            filler[i] = new int[0];
            // actual reported memory
            startMemory = rt.totalMemory() - rt.freeMemory();
            if (startMemory > before) { // check if reported memory has changed
                // ok, we are at the beginning of a new chunk
                start = (i == 0) ? 0 : i + 1;
                return;
            } else if (startMemory < before) {
                throw new AssertionError("GC occurred");
            }
        }
        throw new AssertionError("first threshold not reached");
    }

    /**
     * @return the bytes used by the measured code, or {@link #GC_OCCURRED} if a
     * garbage collection invalidated the measurement.
     */
    public synchronized final long getUsedMemory() {
        long measured = usedMemory();
        filler = null;
        return measured == GC_OCCURRED ? GC_OCCURRED : measured - zero;
    }

    private long usedMemory() {
        for (i=start; i<filler.length; i++) {
            filler[i] = new int[0]; // [minAllocableMemory] bytes
            usedMem = rt.totalMemory() - rt.freeMemory() - startMemory;
            if (usedMem > 0) {
                return usedMem - ((i - start) * minAllocableMemory);
            } else if (usedMem < 0) {
                // gc happend
                // System.out.println("GC occurred, returning " + Long.MIN_VALUE);
                return GC_OCCURRED;
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
