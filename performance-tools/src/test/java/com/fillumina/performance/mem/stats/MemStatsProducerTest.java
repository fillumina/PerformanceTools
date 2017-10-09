package com.fillumina.performance.mem.stats;

import com.fillumina.performance.executor.test.LfsrRunnable;
import com.fillumina.performance.util.stats.Measure;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * {@link LfsrRunnable} doesn't use and allocate any memory.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MemStatsProducerTest {

    public static void main(final String[] args) {
        Measure usedMeasure = OldMemStatsProducer
                .createUsed()
                .memoryUsage(new LfsrRunnable())
                .getAssertable()
                .getFirstMeasure();

        Measure allocMeasure = OldMemStatsProducer
                .createAllocated()
                .memoryUsage(new LfsrRunnable())
                .getAssertable()
                .getFirstMeasure();

        System.out.println("used value = " + usedMeasure.toString());
        System.out.println("");
        System.out.println("alloc value= " + allocMeasure.toString());
    }

    @Test
    public void shouldEvaluateZeroBytesUsed() {
        Measure measure = OldMemStatsProducer
                .createUsed()
                .memoryUsage(new LfsrRunnable())
                .getAssertable()
                .getFirstMeasure();

        assertEquals(0, measure.getMean(), 0);
    }

    @Test
    public void shouldEvaluateZeroBytesAllocated() {
        Measure measure = OldMemStatsProducer
                .createAllocated()
                .memoryUsage(new LfsrRunnable())
                .getAssertable()
                .getFirstMeasure();

        assertEquals(0, measure.getMean(), 0);
    }
}
