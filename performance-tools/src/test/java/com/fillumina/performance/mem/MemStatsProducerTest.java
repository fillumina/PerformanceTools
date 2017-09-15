package com.fillumina.performance.mem;

import com.fillumina.performance.infrastructure.test.LfsrRunnable;
import com.fillumina.performance.mem.sample.AllocatedMemSampleProducer;
import com.fillumina.performance.mem.sample.UsedMemSampleProducer;
import com.fillumina.performance.util.unit.LoggedDimensionalOnlineMeasure;
import org.junit.Test;

/**
 * {@link LfsrRunnable} doesn't use and allocate any memory.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MemStatsProducerTest {

    public static void main(final String[] args) {
        LoggedDimensionalOnlineMeasure usedMeasure = UsedMemSampleProducer
                .createMemAnalyzer()
                .memoryUsage(new LfsrRunnable());

        LoggedDimensionalOnlineMeasure allocMeasure = UsedMemSampleProducer
                .createMemAnalyzer()
                .memoryUsage(new LfsrRunnable());

        System.out.println("used value = " + usedMeasure.toString());
        System.out.println(usedMeasure.getLogMessages());
        System.out.println("");
        System.out.println("alloc value= " + allocMeasure.toString());
        System.out.println(allocMeasure.getLogMessages());
    }

    @Test
    public void shouldEvaluateZeroBytesUsed() {
        UsedMemSampleProducer
                .createMemAnalyzer()
                .memoryUsage(new LfsrRunnable())
                .assertEquals(0);
    }

    @Test
    public void shouldEvaluateZeroBytesAllocated() {
        AllocatedMemSampleProducer
                .createMemAnalyzer()
                .memoryUsage(new LfsrRunnable())
                .assertEquals(0);
    }
}
