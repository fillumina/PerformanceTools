package com.fillumina.performance.mem.sample;

import com.fillumina.performance.mem.AssertMem;
import com.fillumina.performance.mem.MemAllocator;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AllocatedMemSampleExecutorTest {

    public static void main(final String[] args) {
        System.out.println(MemoryEvaluatorInfo.INSTANCE.getDebugString());
        AllocatedMemSampleExecutorTest test =
                new AllocatedMemSampleExecutorTest();
        test.shouldTestNoMemoryAllocated();
        test.shouldTestSomeMemoryAllocated();
    }


    @Test
    public void shouldTestNoMemoryAllocated() {
        AssertMem.allocated(0, ()->{});
    }

    @Test
    public void shouldTestSomeMemoryAllocated() {
        MemAllocator allocator = new MemAllocator();
        AssertMem.allocated(16, ()->allocator.allocate(new int[0]) );
    }

}
