package com.fillumina.performance.accuracy.mem;

import com.fillumina.performance.executor.test.SafeSink;
import com.fillumina.performance.mem.MemAnalyzer;
import com.fillumina.performance.mem.MemUtil;
import com.fillumina.performance.mem.sample.MemoryEvaluatorInfo;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Check for the upper limit of mem evaluation accuracy.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class HugeMemoryTest {

    /**
     * The current memory estimator is not able to report accurately values
     * bigger than a certain amount. It depends on the accuracy of the
     * {@link Runtime#totalMemory() } method.
     * Use {@link MemoryEvaluatorInfo#calculateMaxDetectableMemory(java.lang.Appendable) }
     * to know which is the maximum memory correctly reported.
     */
    @Test
    public void shouldEvaluateABigObject() {
        // it seems that is a safe value
        final int size = getMaxByteArrayAllocableSize();
        final MemoryEvaluatorInfo info = MemoryEvaluatorInfo.INSTANCE;

        final String message = info.getDebugString();
        final int expected = size + info.getMinimalAllocableMemory();
        final int tolerance = 0;
        final long memUsed = MemAnalyzer.used(
                () -> SafeSink.drain(new byte[size]));

        assertEquals(message, expected, memUsed, tolerance);

//        System.out.println("shouldEvaluateABigObject:\n" + message);
    }

    private static int getMaxByteArrayAllocableSize() {
        long max = MemoryEvaluatorInfo.INSTANCE.getMaxDetectableMemory();
        int min = MemoryEvaluatorInfo.INSTANCE.getMinimalAllocableMemory();
        int size = (int) MemUtil.alignUp( (max >> 1) - min, 8);
        //System.out.println("MAX=" + max + ", MIN=" + min + ", SIZE=" + size);
        //System.out.println(MemoryEvaluatorInfo.INSTANCE.toString());
        return size;
    }
}
