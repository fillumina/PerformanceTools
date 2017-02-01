package com.fillumina.performance.mem;

import com.fillumina.performance.mem.sample.AllocatedMemConsumptionExecutor;
import com.fillumina.performance.mem.sample.UsedMemConsumptionExecutor;
import com.fillumina.performance.speed.sample.Testable;
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
                .memoryUsage(Testable.NO_MEM);

        LoggedDimensionalOnlineMeasure allocMeasure = UsedMemConsumptionExecutor
                .createMemAnalyzer()
                .memoryUsage(Testable.NO_MEM);

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
                .memoryUsage(Testable.NO_MEM)
                .assertEquals(0);
    }

    @Test
    public void shouldTestableNULLAllocatedZeroBytes() {
        AllocatedMemConsumptionExecutor
                .createMemAnalyzer()
                .memoryUsage(Testable.NO_MEM)
                .assertEquals(0);
    }
}
