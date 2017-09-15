package com.fillumina.performance.mem;

import com.fillumina.performance.infrastructure.test.SafeSink;
import com.fillumina.performance.mem.sample.AllocatedMemSampleProducer;
import com.fillumina.performance.mem.sample.UsedMemSampleProducer;
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
        return AllocatedMemSampleProducer.createMemAnalyzer()
                .memoryUsage(new Runnable() {
                    final Object[] array = new Object[1000];
                    int i = -1;

                    @Override
                    public void run() {
                        i++;
                        array[i] = new byte[size];
                        SafeSink.drain(array[i]);
                    }
                });
    }

    @Test
    public void shouldEstimateUsedMemory() {
        usedMemoryForByteArrayOfSize(23).assertEquals(16 + 23 + 1);
    }

    private static MemMeasure
        usedMemoryForByteArrayOfSize(final int size) {
        return UsedMemSampleProducer.createMemAnalyzer()
                .memoryUsage(new Runnable() {

                    @Override
                    public void run() {
                        SafeSink.drain(new byte[size]);
                    }
                });
    }

}
