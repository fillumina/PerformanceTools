package com.fillumina.performance.mem;

import com.fillumina.performance.mem.sample.AllocatedMemConsumptionExecutor;
import com.fillumina.performance.mem.sample.UsedMemConsumptionExecutor;
import com.fillumina.performance.speed.sample.Testable;
import static org.junit.Assert.assertEquals;
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
        LoggedDimensionalOnlineMeasure measure = UsedMemConsumptionExecutor
                .createMemAnalyzer()
                .memoryUsage(Testable.NO_MEM);
        long value = (long) measure.getMean();
        assertEquals(measure.getLogMessages(), 0, value);
    }

    @Test
    public void shouldTestableNULLAllocatedZeroBytes() {
        LoggedDimensionalOnlineMeasure measure = AllocatedMemConsumptionExecutor
                .createMemAnalyzer()
                .memoryUsage(Testable.NO_MEM);
        long value = (long) measure.getMean();
        assertEquals(measure.getLogMessages(), 0, value);
    }
}
