package com.fillumina.performance.mem.sample;

import com.fillumina.performance.mem.LoggedDimensionalOnlineMeasure;
import com.fillumina.performance.mem.MemAnalyzer;
import com.fillumina.performance.speed.sample.AbstractTestable;

/**
 * Returns info about the current JVM memory allocator derived by measurements.
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

    // TODO if it allocates different objects it's difficult to calcualte padding
    public void assertEquals(long expected,
            LoggedDimensionalOnlineMeasure resultMeasure)
            throws AssertionError {
        long result = (long) resultMeasure.getMean();
        if (expected != result) {
            String format = String.format(
                    "DEBUG INFO: %serror    = expected %,d was %,d",
                    resultMeasure.getLogMessages(), expected, result);
            throw new AssertionError(format);
        }
    }

    /** Internal debug string, not part of the API. */
    public String getDebugString() {
        return MemoryConsumption.INSTANCE.toString();
    }

    public long calculateMaximumAccuracyValue() {
        final MemAnalyzer memAnalyzer =
                UsedMemConsumptionExecutor.createMemAnalyzer();
        long lastUsed = -1;
        for (int i=4; i<24; i++) {
            final int size = (1 << i);
            final LoggedDimensionalOnlineMeasure measure =
                memAnalyzer.memoryUsage(new SizeTest(size));
            final int used = (int) measure.getMean();
            if (lastUsed > 0 && Math.abs(lastUsed - used) > 16) {
                return size;
            }
            lastUsed = used;
        }
        return 1 << 24;
    }

    private static class SizeTest extends AbstractTestable {
        private final int size;

        public SizeTest(int size) {
            this.size = size;
        }

        @Override
        public Object test() {
            return new byte[size];
        }
    }
}
