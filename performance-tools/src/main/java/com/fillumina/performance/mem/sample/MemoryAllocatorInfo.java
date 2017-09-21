package com.fillumina.performance.mem.sample;

import com.fillumina.performance.mem.MemUtil;
import com.fillumina.performance.executor.test.SafeSink;
import com.fillumina.performance.util.ExpBinarySearcher;
import java.io.IOException;

/**
 * Returns info about the current JVM memory allocator.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MemoryAllocatorInfo {
    public static final MemoryAllocatorInfo INSTANCE =
            new MemoryAllocatorInfo();

    private MemoryAllocatorInfo() {}

    /** @return Minimum amount of allocable memory in bytes (actually 16). */
    public int getMinimalAllocableMemory() {
        return MemoryConsumption.INSTANCE.getMinimalAllocableMemory();
    }

    /** @return Memory padding in bytes (actually 8). */
    public long getMemoryPadding() {
        return MemoryConsumption.INSTANCE.getAlignment();
    }

    /** Internal debug string, not part of the API. */
    public String getDebugString() {
        return MemoryConsumption.INSTANCE.toString();
    }

    /**
     * Returns an estimation of the actual bytes allocated when requesting x bytes.
     *
     * @param x the bytes to allocate
     * @return the actually allocated bytes (estimation)
     */
    public long alignWithPadding(long x) {
        return MemUtil.alignUp(x, getMemoryPadding());
    }

    /**
     * The current algorithm to evaluate memory consumption is quite
     * accurate for low memory usage but returns invalid results if the
     * allocated memory is over a certain threshold. This method tries to
     * calculate that threshold.
     * <p>
     * <b>WARNING:</b> it might take a while (about 15 minutes).
     *
     * @param  log an {@link Appendable} to log events. Setting {@code null}
     *         disable logging.
     * @return the upper limit of allocated memory accurately returned by
     *         the memory allocator.
     */
    public long calculateMemoryAccuracyThreshold(final Appendable log) {
        return ExpBinarySearcher.search(1 << 24, new Comparable<Integer>() {
            private int arrayMemoryAllocation =
                    MemoryConsumption.INSTANCE.getMinimalAllocableMemory();
            private int alignment = (int)
                    MemoryConsumption.INSTANCE.getAlignment();

            @Override
            public int compareTo(final Integer o) {
                int mem = (int) UsedMemSampleExecutor.INSTANCE
                    .execute((Runnable) () -> {
                        SafeSink.drain(new byte[o]);
                    });
                final int value = o + arrayMemoryAllocation;
                final int diff = (int) MemUtil.alignUp(value, alignment) - mem;
                if (diff == 0) {
                    log("memory evaluation of byte[", o, "] correct");
                    return -1;
                } else {
                    log("memory evaluation of byte[", o, "] incorrect by ",
                            diff, " bytes");
                    return 1;
                }
            }

            private void log(Object... message) {
                if (log != null) {
                    try {
                        for (Object m : message) {
                            log.append(m.toString());
                        }
                        log.append(System.lineSeparator());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }

    public static void main(final String[] args) {
        long maxMem = MemoryAllocatorInfo.INSTANCE
                .calculateMemoryAccuracyThreshold(System.out);
        System.out.println("max memory assessable= " + maxMem);
    }
}
