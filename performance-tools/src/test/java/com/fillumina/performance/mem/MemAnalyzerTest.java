package com.fillumina.performance.mem;

import com.fillumina.performance.infrastructure.LfsrRunnable;
import com.fillumina.performance.mem.sample.AllocatedMemConsumptionExecutor;
import com.fillumina.performance.mem.sample.UsedMemConsumptionExecutor;
import com.fillumina.performance.util.unit.LoggedDimensionalOnlineMeasure;
import org.junit.Test;

/**
 * {@link LfsrRunnable} doesn't use and allocate any memory.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MemAnalyzerTest {

    public static void main(final String[] args) {
        LoggedDimensionalOnlineMeasure usedMeasure = UsedMemConsumptionExecutor
                .createMemAnalyzer()
                .memoryUsage(new LfsrRunnable());

        LoggedDimensionalOnlineMeasure allocMeasure = UsedMemConsumptionExecutor
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
        UsedMemConsumptionExecutor
                .createMemAnalyzer()
                .memoryUsage(new LfsrRunnable())
                .assertEquals(0);
    }

    @Test
    public void shouldEvaluateZeroBytesAllocated() {
        AllocatedMemConsumptionExecutor
                .createMemAnalyzer()
                .memoryUsage(new LfsrRunnable())
                .assertEquals(0);
    }
}
