package com.fillumina.performance.mem;

import com.fillumina.performance.infrastructure.test.SafeSink;
import com.fillumina.performance.mem.sample.AllocatedMemSampleProducer;
import com.fillumina.performance.mem.sample.UsedMemSampleProducer;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AllocationChunkMemoryTest {

    /**
     * Discovers how many bytes the current JVM allocates for an integer
     * array of given size.
     */
    public static void main(final String[] args) {
        for (int i=0; i<40; i++) {
            System.out.println(
                    "used memory for array of size = " + i +
                    ", \trequired bytes = " + (i + 16) +
                    ", \tusing bytes = " +
                        usedMemoryForByteArrayOfSize(i).getMean() );
        }
    }

    @Test
    public void shouldEstimateAllocatedMemory() {
        allocatedMemoryForByteArrayOfSize(23).assertEquals(16 + 23 + 1);
    }

    private static MemMeasure allocatedMemoryForByteArrayOfSize(int size) {
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

    private static MemMeasure usedMemoryForByteArrayOfSize(int size) {
        return UsedMemSampleProducer.createMemAnalyzer()
                .memoryUsage(() -> { SafeSink.drain(new byte[size]); });
    }

}
