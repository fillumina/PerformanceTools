package com.fillumina.performance.mock;

import com.fillumina.performance.speed.stats.SingleSpeedStats;
import com.fillumina.performance.util.unit.DimensionalMeasure;
import com.fillumina.performance.util.unit.DimensionalOnlineMeasure;
import com.fillumina.performance.util.unit.IntervalUnit;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MockSingleSpeedStatsTest {

    @Test
    public void shouldCreateASingleStats() {
        DimensionalMeasure timeNs =
                new DimensionalOnlineMeasure(IntervalUnit.NANOSECONDS, 12345);

        SingleSpeedStats single = MockSingleSpeedStats
                .builder()
                .name("alpha")
                .originalSamples(100)
                .samples(78)
                .timeNs(timeNs)
                .totalIterations(10_000)
                .totalTime(12345 * 10_000)
                .build();

        assertEquals("alpha", single.getName());
        assertEquals(100, single.getOriginalSamples());
        assertEquals(78, single.getSamples());
        assertEquals(timeNs, single.getElapsedNanosecondsPerCycle());
        assertEquals(10_000, single.getTotalIterations());
        assertEquals(12345 * 10_000, single.getTotalTime());
    }

}
