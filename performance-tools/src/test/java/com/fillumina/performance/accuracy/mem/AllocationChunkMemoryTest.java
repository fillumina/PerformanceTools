package com.fillumina.performance.accuracy.mem;

import com.fillumina.performance.mem.sample.AllocatedMemSampleProducer;
import com.fillumina.performance.mem.sample.UsedMemSampleProducer;
import com.fillumina.performance.executor.test.SafeSink;
import static org.junit.Assert.assertEquals;
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
                    ", \tusing bytes = " + usedMemoryForByteArrayOfSize(i));
        }
    }

    @Test
    public void shouldEstimateAllocatedMemory() {
        assertEquals(16 + 23 + 1, allocatedMemoryForByteArrayOfSize(23));
    }

    private static long allocatedMemoryForByteArrayOfSize(int size) {
        return new AllocatedMemSampleProducer()
                .execute(new Runnable() {
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
        assertEquals(16 + 23 + 1, usedMemoryForByteArrayOfSize(23));
    }

    private static long usedMemoryForByteArrayOfSize(int size) {
        return new UsedMemSampleProducer()
                .execute(() -> SafeSink.drain(new byte[size]));
    }

}
