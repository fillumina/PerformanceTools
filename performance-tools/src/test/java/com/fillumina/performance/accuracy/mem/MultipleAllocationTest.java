package com.fillumina.performance.accuracy.mem;

import com.fillumina.performance.executor.test.SafeSink;
import com.fillumina.performance.mem.AssertMem;
import com.fillumina.performance.mem.MemAllocator;
import com.fillumina.performance.mem.MemAnalyzer;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MultipleAllocationTest {

    public static void main(final String[] args) {
        for (int i=0; i<40; i++) {
            final int size = i;
            System.out.println(
                    "used for size = " + i +
                    ", \tbytes = " +
                            MemAnalyzer.used(() -> SafeSink.drain(new byte[size])) +
                    ", \texpected = " + (i + 16));
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
        AssertMem.used(16 + 23 + 1, () -> SafeSink.drain(new byte[23]) );
    }
}
