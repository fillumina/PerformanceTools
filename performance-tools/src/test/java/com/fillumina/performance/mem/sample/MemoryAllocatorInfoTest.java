package com.fillumina.performance.mem.sample;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MemoryAllocatorInfoTest {

    // presently returns 498048 which is good enough (in about 16 min)
    public static void main(final String[] args) {
        long accuracy = MemoryAllocatorInfo.INSTANCE
                .calculateMemoryAccuracyThreshold(System.out);
        System.out.println("mem accuracy = " + accuracy);
    }
}
