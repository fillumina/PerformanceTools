package com.fillumina.performance.mem;

import com.fillumina.performance.mem.sample.AllocatedMemConsumptionExecutor;
import com.fillumina.performance.mem.sample.MemoryAllocatorInfo;
import com.fillumina.performance.mem.sample.UsedMemConsumptionExecutor;
import com.fillumina.performance.speed.sample.AbstractTestable;
import org.junit.Test;

/**
 * Check for precision up to 1 << 18 = 262,144 which seems to be the last
 * value for which results are given with a certain accuracy.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class HugeMemoryTest {

    public static void main(final String[] args) {
        System.out.println(MemoryAllocatorInfo.INSTANCE.getDebugString());
        final int start = 1 << 19;
        final int end = 1 << 20;
        final int step = 1 << 12;
        for (int i=start; i<end; i+= step) {
            int size = i;
            final LoggedDimensionalOnlineMeasure measure =
                    usedMemoryForByteArrayOfSize(size);
            final int used = (int) measure.getMean();
            final String str = String.format(
                    "i = %d \tsize = %,d \tresult = %,d \tdiff = %,d",
                    i, size, used, size - used);
            System.out.println(measure.getLogMessages());

            System.out.println(str);
        }
//        for (int i=491_520; i<(1 << 20); i+=32_768) {
//            final int size = i;
//            final int used = (int) usedMemoryForByteArrayOfSize(size).getMean();
//            final String str = String.format("i = %,d \tbytes = %,d \tdiff = %,d",
//                            size, used, size - used);
//            System.out.println(str);
//        }
    }

    @Test
    public void shouldEstimateAllocatedMemory() {
        final int bytes = 1 << 18; // 262,144
        LoggedDimensionalOnlineMeasure m =
                allocatedMemoryForByteArrayOfSize(bytes);
        MemoryAllocatorInfo.INSTANCE.assertEquals(16 + bytes, m);
    }

    private static LoggedDimensionalOnlineMeasure
        allocatedMemoryForByteArrayOfSize(final int size) {
        return AllocatedMemConsumptionExecutor.createMemAnalyzer()
                .memoryUsage(new AbstractTestable() {
                    final Object[] array = new Object[1000];
                    int i = -1;

                    @Override
                    public Object test() {
                        i++;
                        array[i] = new byte[size];
                        return array[i];
                    }
                });
    }

    //TODO test if the error repeats if allocating 2 arrays of total size > 2Mb

    @Test
    public void shouldEstimateUsedMemory2Mb() {
        final int bytes = 1 << 18; // 262,144
        LoggedDimensionalOnlineMeasure m = usedMemoryForByteArrayOfSize(bytes);
        MemoryAllocatorInfo.INSTANCE.assertEquals(16 + bytes, m);
    }

    private static LoggedDimensionalOnlineMeasure
        usedMemoryForByteArrayOfSize(final int size) {
        return UsedMemConsumptionExecutor.createMemAnalyzer()
                .memoryUsage(new AbstractTestable() {
                    @Override
                    public Object test() {
                        return new byte[size];
                    }
                });
    }

    private static LoggedDimensionalOnlineMeasure
        usedMemoryForByteArrayOfDoubleSize(final int size) {
        return UsedMemConsumptionExecutor.createMemAnalyzer()
                .memoryUsage(new AbstractTestable() {

                    @Override
                    public Object test() {
                        byte[] a1 = new byte[size >> 1];
                        byte[] a2 = new byte[size >> 1];
                        return a1.length + a2.length;
                    }
                });
    }
}
