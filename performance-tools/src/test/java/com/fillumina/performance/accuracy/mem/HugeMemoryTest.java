package com.fillumina.performance.accuracy.mem;

import com.fillumina.performance.mem.sample.MemoryAllocatorInfo;
import com.fillumina.performance.mem.stats.OLD_MemStatsProducer;
import com.fillumina.performance.executor.test.SafeSink;
import com.fillumina.performance.util.stats.Measure;
import java.util.Locale;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Check for precision up to 1 << 18 = 262,144 which seems to be the last
 * value for which results are given with accuracy.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class HugeMemoryTest {

    public static void main(final String[] args) {
        System.out.println(MemoryAllocatorInfo.INSTANCE.getDebugString());
        for (int i=1; i<22; i++) {
            int size = 1 << i;
            final Measure measure = usedMemoryForByteArrayOfDoubleSize(size);
            final int used = (int) measure.getMean();
            final String str = String.format(Locale.US,
                    "i = %d \tsize = %,d \tresult = %,d \tdiff = %,d",
                    i, size, used, size - used);
//            System.out.println(measure.getLogMessages());

            System.out.println(str);
        }
//        for (int i=491_520; i<(1 << 20); i+=32_768) {
//            final int size = i;
//            final int used = (int) usedMemoryForByteArrayOfSize(size).getMean();
//            final String str = String.format(Locale.US,"i = %,d \tbytes = %,d \tdiff = %,d",
//                            size, used, size - used);
//            System.out.println(str);
//        }
    }

    @Test
    public void shouldEstimateAllocatedMemory() {
        final int bytes = 1 << 18; // 262,144
        assertEquals(16 + bytes, allocatedMemoryForByteArrayOfSize(bytes));
    }

    private static Measure allocatedMemoryForByteArrayOfSize(int size) {
        return OLD_MemStatsProducer.createAllocated()
                .memoryUsage(new Runnable() {
                    final Object[] array = new Object[1000];
                    int i = -1;

                    @Override
                    public void run() {
                        i++;
                        array[i] = new byte[size];
                        SafeSink.drain(array[i]);
                    }
                })
                .getAssertable()
                .getFirstMeasure();
    }

    @Test
    public void shouldEstimateHighUsedMemory() {
        final int bytes = 1 << 18; // 262,144
        assertEquals(16 + bytes, usedMemoryForByteArrayOfSize(bytes));
    }

    private static Measure usedMemoryForByteArrayOfSize(final int size) {
        return OLD_MemStatsProducer.createUsed()
                .memoryUsage(() -> { SafeSink.drain(new byte[size]); })
                .getAssertable()
                .getFirstMeasure();
    }

    private static Measure usedMemoryForByteArrayOfDoubleSize(int size) {
        return OLD_MemStatsProducer.createUsed()
                .memoryUsage(() -> {
                    byte[] a1 = new byte[size >> 1];
                    byte[] a2 = new byte[size >> 1];
                    SafeSink.drain(a1.length + a2.length);
                })
                .getAssertable()
                .getFirstMeasure();
    }


    /**
     * The current memory estimator is not able to report accurately values
     * bigger than a certain amount. It depends on the accuracy of the
     * {@link Runtime#totalMemory() } method.
     * Use {@link MemoryAllocatorInfo#calculateMemoryAccuracyThreshold(java.lang.Appendable) }
     * to know which is the maximum memory correctly reported.
     */
    @Test
    public void shouldEvaluateABigObject() {
        // it seems that is a safe value
        final int size = 1 << 17;

        final String message = MemoryAllocatorInfo.INSTANCE.getDebugString();
        final int expected = size + 16;
        final int tolerance = 0;
        final long memUsed = (long) OLD_MemStatsProducer.createUsed()
                .memoryUsage(() -> {SafeSink.drain(new byte[size]);})
                .getAssertable()
                .getFirstMeasure()
                .getMean();

        assertEquals(message, expected, memUsed, tolerance);
    }

}
