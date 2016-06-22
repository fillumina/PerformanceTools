package com.fillumina.performance.mem;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
final class MemoryConsumption {
    public static final MemoryConsumption INSTANCE = new MemoryConsumption();
    private static final int FILLER_SIZE = 1 << 19;

    private final Runtime rt;
    private final int byteGranularity;
    private long zero = 0;
    private Object[] filler;
    private long usedMemoryBefore;
    private int start;
    private long after, before;
    private int i, j;

    private MemoryConsumption() {
        rt = Runtime.getRuntime();
        byteGranularity = calculateGranularity();
        start();
        zero = getUsedMemory();
    }

    private int calculateGranularity() throws AssertionError {
        filler = null;
        System.gc();
        try {
            Thread.sleep(250);
        } catch (InterruptedException e) {
            // helps jvm to perform a gc
        }
        filler = new Object[FILLER_SIZE];
        reachFirstThreshold();
        start = reachFirstThreshold();
        before = rt.totalMemory() - rt.freeMemory();
        for (i=start; i<filler.length; i++) {
            filler[i] = new int[0]; // 16 bytes
            after = rt.totalMemory() - rt.freeMemory() - before;
            if (after > 0) {
                double mem = after * 1.0 / (i - start);
                return (int) Math.floor(mem);
            }
        }
        throw new AssertionError("memory assessment initialization failed: " +
                toString());
    }

    public final void start() {
        for (i=0; i<filler.length; i++) {
            filler[i] = null;
        }
        filler = null;
        filler = new Object[FILLER_SIZE];
        System.gc();
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
        }
        start = reachFirstThreshold();
        usedMemoryBefore = rt.totalMemory() - rt.freeMemory();
    }

    private int reachFirstThreshold() {
        before = rt.totalMemory() - rt.freeMemory();
        for (i=0; i<filler.length; i++) {
            filler[i] = new int[0];
            if (rt.totalMemory() - rt.freeMemory() - before > 0) {
                //System.out.println("k="+idx+"\tmem="+after);
                return i;
            }
        }
        throw new AssertionError("threshold memory assessment failed: " + toString());
    }

    public final long getUsedMemory() {
        for (i=start; i<filler.length; i++) {
            filler[i] = new int[0];
            after = rt.totalMemory() - rt.freeMemory() - usedMemoryBefore;
            if (after > 0) {
                return after - ((i - start - 1) * byteGranularity) - zero;
            }
        }
        throw new AssertionError("memory assessment failed: " + toString());
    }

    public int getByteGranularity() {
        return byteGranularity;
    }

    @Override
    public String toString() {
        return "MemoryConsumption{" +
                "byteGranularity=" + byteGranularity +
                ", zero=" + zero +
                ", start=" + start +
                ", usedMemoryBefore=" + usedMemoryBefore +
                ", after=" + after +
                ", filler_length=" + filler.length +
                ", idx=" + i + '}';
    }
}
