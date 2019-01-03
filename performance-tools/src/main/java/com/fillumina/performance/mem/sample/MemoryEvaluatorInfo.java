package com.fillumina.performance.mem.sample;

import com.fillumina.performance.mem.MemUtil;
import com.fillumina.performance.util.ExpBinarySearcher;
import com.fillumina.performance.util.formatter.TableFormatter;
import java.io.IOException;

/**
 * Returns info about the current JVM memory allocator.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MemoryEvaluatorInfo {
    public static final MemoryEvaluatorInfo INSTANCE =
            new MemoryEvaluatorInfo();

    private long maxDetectableMemory = Integer.MIN_VALUE;

    private MemoryEvaluatorInfo() {}

    /**
     * @return Minimum amount of allocable memory in bytes (actually 16).
     *         This is also the memory used by an empty array or Object.
     */
    public int getMinimalAllocableMemory() {
        return MemoryConsumption.INSTANCE.getMinimalAllocableMemory();
    }

    /** @return Memory padding in bytes (actually 8). */
    public long getMemoryPadding() {
        return MemoryConsumption.INSTANCE.getAlignment();
    }

    /** Internal debug string, not part of the API (might change). */
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

    public long getMaxDetectableMemory() {
        if (maxDetectableMemory == Integer.MIN_VALUE) {
            maxDetectableMemory = calculateMaxDetectableMemory(System.out);
        }
        return maxDetectableMemory;
    }

    public long calculateMaxDetectableMemory() {
        return calculateMaxDetectableMemory(null);
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
    public long calculateMaxDetectableMemory(final Appendable log) {
        return ExpBinarySearcher.search(1 << 24, new Comparable<Integer>() {
            private final int sizeOfTheEmptyArrayObject =
                    MemoryConsumption.INSTANCE.getMinimalAllocableMemory();
            private final int alignment = (int)
                    MemoryConsumption.INSTANCE.getAlignment();

            @Override
            public int compareTo(final Integer size) {
                int mem = (int) UsedMemSampleExecutor.INSTANCE.execute(() -> {
                    byte[] array = new byte[size];
                    // forces the array to not be discarded by optimizations
                    if (array.hashCode() == 0) {
                        throw new AssertionError();
                    }
                });
                final int expected = size + sizeOfTheEmptyArrayObject;
                final int diff = (int) MemUtil.alignUp(expected, alignment) - mem;
                if (diff == 0) {
                    log("memory evaluation of byte[", size, "] correct");
                    return -1;
                } else {
                    log("memory evaluation of byte[", size, "] incorrect by ",
                            diff, " bytes, mem=", mem,
                            ", align=" + alignment,
                            ", emptyArraySize=", sizeOfTheEmptyArrayObject);
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

    @Override
    public String toString() {
        return new TableFormatter().header(getClass().getSimpleName())
                .param("max detectable memory", getMaxDetectableMemory())
                .param("minimal allocable memory", getMinimalAllocableMemory())
                .param("memory padding", getMemoryPadding())
                .toString();
    }

    public static void main(final String[] args) {
        evaluateMaxDetectableMemory();
    }

    private static void evaluateMaxDetectableMemory() {
        System.out.println("min allocalble memory= " +
                MemoryConsumption.INSTANCE.getMinimalAllocableMemory());
        long maxMem = MemoryEvaluatorInfo.INSTANCE
                .calculateMaxDetectableMemory(System.out);
        System.out.println("max memory assessable= " + maxMem);
    }
}
