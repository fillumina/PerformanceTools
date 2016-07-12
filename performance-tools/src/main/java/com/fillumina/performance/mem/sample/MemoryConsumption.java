package com.fillumina.performance.mem.sample;

/**
 * Calculates the used memory.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
final class MemoryConsumption {
    public static final MemoryConsumption INSTANCE = new MemoryConsumption();

    private final Runtime rt;
    private final int byteGranularity; // should be 16
    private long zero = 0;             // should be 32
    private Object[] filler;
    private int fillerSize = 2 << 21;
    private long usedMemoryBefore;
    private int start;
    private long after, before;
    private int i;

    private MemoryConsumption() {
        rt = Runtime.getRuntime();
        byteGranularity = calculateGranularity();
        zero = calculateZero();
        System.out.println(toString());
    }

    private long calculateZero() {
        long z, min = Long.MAX_VALUE;
        for (int k=0; k<20; k++) {
            start();
            z = getUsedMemory();
            if (z < min) {
                min = z;
            }
            //System.out.println("z=" + z);
        }
        return min;
    }

    private synchronized int calculateGranularity() throws AssertionError {
        for (int j = 20; j< 24; j++) {
            fillerSize = 2 << j;
            filler = null;
            System.gc();
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                // helps jvm to perform a gc
            }
            filler = new Object[fillerSize];
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
        }
        throw new AssertionError("memory assessment initialization failed: " +
                toString());
    }

    public synchronized final void start() {
        for (i=0; i<filler.length; i++) {
            filler[i] = null;
        }
        filler = null;
        filler = new Object[fillerSize];
        start = reachFirstThreshold();
        usedMemoryBefore = rt.totalMemory() - rt.freeMemory();
    }

    private synchronized int reachFirstThreshold() {
        for (int k=0; k<10; k++) {
            System.gc();
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
            }
            before = rt.totalMemory() - rt.freeMemory();
            for (i=0; i<filler.length; i++) {
                filler[i] = new int[0];
                after = rt.totalMemory() - rt.freeMemory() - before;
                if (after > 0) {
                    return i + 1;
                } else if (after < 0) {
                    // a garbage collection has happened
                    break;
                }
            }
        }
        throw new AssertionError("threshold memory assessment failed: " +
                toString());
    }

    public synchronized final long getUsedMemory() {
        for (i=start; i<filler.length; i++) {
            filler[i] = new int[0];
            after = rt.totalMemory() - rt.freeMemory() - usedMemoryBefore;
            if (after > 0) {
                return after - ((i - start) * byteGranularity) - zero;
            } else if (after < 0) {
                // a garbage collection has happened
                return Long.MIN_VALUE; // so it is filtered out as an outlier
            }
        }
        throw new AssertionError("used memory assessment failed: " +
                toString());
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
