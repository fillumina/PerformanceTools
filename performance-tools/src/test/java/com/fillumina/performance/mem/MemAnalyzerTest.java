package com.fillumina.performance.mem;

import com.fillumina.performance.infrastructure.LfsrTestable;
import com.fillumina.performance.mem.sample.AllocatedMemConsumptionExecutor;
import com.fillumina.performance.mem.sample.UsedMemConsumptionExecutor;
import com.fillumina.performance.util.unit.LoggedDimensionalOnlineMeasure;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MemAnalyzerTest {

    public static void main(final String[] args) {
        LoggedDimensionalOnlineMeasure usedMeasure = UsedMemConsumptionExecutor
                .createMemAnalyzer()
                .memoryUsage(new LfsrTestable());

        LoggedDimensionalOnlineMeasure allocMeasure = UsedMemConsumptionExecutor
                .createMemAnalyzer()
                .memoryUsage(new LfsrTestable());

        System.out.println("used value = " + usedMeasure.toString());
        System.out.println(usedMeasure.getLogMessages());
        System.out.println("");
        System.out.println("alloc value= " + allocMeasure.toString());
        System.out.println(allocMeasure.getLogMessages());
    }

    @Test
    public void shouldTestableNULLUseZeroBytes() {
        UsedMemConsumptionExecutor
                .createMemAnalyzer()
                .memoryUsage(new LfsrTestable())
                .assertEquals(0);
    }

    @Test
    public void shouldTestableNULLAllocatedZeroBytes() {
        AllocatedMemConsumptionExecutor
                .createMemAnalyzer()
                .memoryUsage(new LfsrTestable())
                .assertEquals(0);
    }
}
