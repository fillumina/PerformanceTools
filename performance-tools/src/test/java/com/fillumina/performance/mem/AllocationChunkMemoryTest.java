package com.fillumina.performance.mem;

import com.fillumina.performance.mem.sample.AllocatedMemConsumptionExecutor;
import com.fillumina.performance.mem.sample.MemoryAllocatorInfo;
import com.fillumina.performance.mem.sample.UsedMemConsumptionExecutor;
import com.fillumina.performance.speed.sample.AbstractTestable;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AllocationChunkMemoryTest {

    public static void main(final String[] args) {
        for (int i=0; i<40; i++) {
            System.out.println(
                    "used memory for size = " + i +
                    ", \tbytes = " + usedMemoryForByteArrayOfSize(i).getMean() +
                    ", \texpected = " + (i + 16));
        }
    }

    @Test
    public void shouldEstimateAllocatedMemory() {
        LoggedDimensionalOnlineMeasure m = allocatedMemoryForByteArrayOfSize(23);
        MemoryAllocatorInfo.INSTANCE.assertEquals(16 + 23 + 1, m);
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

    @Test
    public void shouldEstimateUsedMemory() {
        LoggedDimensionalOnlineMeasure m = usedMemoryForByteArrayOfSize(23);
        MemoryAllocatorInfo.INSTANCE.assertEquals(16 + 23 + 1, m);
    }

    private static LoggedDimensionalOnlineMeasure usedMemoryForByteArrayOfSize(
            final int size) {
        return UsedMemConsumptionExecutor.createMemAnalyzer()
                .memoryUsage(new AbstractTestable() {

                    @Override
                    public Object test() {
                        return new byte[size];
                    }
                });
    }

}
