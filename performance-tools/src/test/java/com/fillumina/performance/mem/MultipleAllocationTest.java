package com.fillumina.performance.mem;

import com.fillumina.performance.mem.sample.AllocatedMemConsumptionExecutor;
import com.fillumina.performance.mem.sample.UsedMemConsumptionExecutor;
import com.fillumina.performance.infrastructure.AbstractTestable;
import com.fillumina.performance.infrastructure.Drain;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MultipleAllocationTest {

    public static void main(final String[] args) {
        for (int i=0; i<40; i++) {
            System.out.println(
                    "used for size = " + i +
                    ", \tbytes = " + usedMemoryForByteArrayOfSize(i) +
                    ", \texpected = " + (i + 16));
        }
    }

    @Test
    public void shouldEstimateAllocatedMemory() {
        allocatedMemoryForByteArrayOfSize(23).assertEquals(16 + 23 + 1);
    }

    private static MemMeasure
        allocatedMemoryForByteArrayOfSize(final int size) {
        return AllocatedMemConsumptionExecutor.createMemAnalyzer()
                .memoryUsage(new AbstractTestable() {
                    final Object[] array = new Object[1000];
                    int i = -1;

                    @Override
                    public void test() {
                        i++;
                        array[i] = new byte[size];
                        Drain.drain(array[i]);
                    }
                });
    }

    @Test
    public void shouldEstimateUsedMemory() {
        usedMemoryForByteArrayOfSize(23).assertEquals(16 + 23 + 1);
    }

    private static MemMeasure
        usedMemoryForByteArrayOfSize(final int size) {
        return UsedMemConsumptionExecutor.createMemAnalyzer()
                .memoryUsage(new AbstractTestable() {

                    @Override
                    public void test() {
                        Drain.drain(new byte[size]);
                    }
                });
    }

}
