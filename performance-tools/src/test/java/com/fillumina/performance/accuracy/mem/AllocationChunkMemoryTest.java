package com.fillumina.performance.accuracy.mem;

import com.fillumina.performance.executor.test.Sink;
import com.fillumina.performance.mem.AssertMem;
import com.fillumina.performance.mem.MemAllocator;
import com.fillumina.performance.mem.MemAnalyzer;
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
            final int v = i;
            System.out.println("used memory for array of size = " + i +
                    ", \trequired bytes = " + (i + 16) +
                    ", \tusing bytes = " +
                        MemAnalyzer.used(() -> Sink.drain(new byte[v])) );
        }
    }

    @Test
    public void shouldEstimateAllocatedMemory() {
        MemAllocator allocator = new MemAllocator(1_000);
        AssertMem.allocated(16 + 23 + 1,
                () -> allocator.allocate(new byte[23]));
    }

    @Test
    public void shouldEstimateUsedMemory() {
        AssertMem.used(16 + 23 + 1, () -> Sink.drain(new byte[23]));
    }

}
