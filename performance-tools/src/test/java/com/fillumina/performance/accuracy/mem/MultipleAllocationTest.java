package com.fillumina.performance.accuracy.mem;

import com.fillumina.performance.mem.stats.MemStatsProducer;
import com.fillumina.performance.executor.test.SafeSink;
import com.fillumina.performance.util.stats.Measure;
import static org.junit.Assert.assertEquals;
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
        assertEquals(16 + 23 + 1, allocatedMemoryForByteArrayOfSize(23));
    }

    private static Measure allocatedMemoryForByteArrayOfSize(final int size) {
        return MemStatsProducer.createAllocated()
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
    public void shouldEstimateUsedMemory() {
        assertEquals(16 + 23 + 1, usedMemoryForByteArrayOfSize(23));
    }

    private static Measure usedMemoryForByteArrayOfSize(final int size) {
        return MemStatsProducer.createUsed()
                .memoryUsage(() -> SafeSink.drain(new byte[size]))
                .getAssertable()
                .getFirstMeasure();
    }

}
