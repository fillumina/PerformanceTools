package com.fillumina.performance.mock;

import com.fillumina.performance.speed.sample.SpeedSample;
import com.fillumina.performance.speed.stats.SingleSpeedStats;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.Ratio;
import com.fillumina.performance.util.unit.DimensionalMeasure;
import com.fillumina.performance.util.unit.DimensionalOnlineMeasure;
import com.fillumina.performance.util.unit.IntervalUnit;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MockPerformanceCreatorTest {

    @Test
    public void shouldCreateASingleStats() {
        DimensionalMeasure timeNs =
                new DimensionalOnlineMeasure(IntervalUnit.NANOSECONDS, 12345);

        SingleSpeedStats single = MockPerformanceCreator
                .singleSpeedStatsBuilder()
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

    @Test
    public void shouldCreateASpeedSample() {
        SpeedSample sample = MockPerformanceCreator.speedSampleBuilder()
                .addTest("first")
                    .timePerOp(10)
                    .iterations(2_000)
                .endTest()
                .addTest("second")
                    .timePerOp(50)
                    .iterations(500)
                .endTest()
                .createSample();

        assertEquals(2_000 * 10 + 50 * 500, sample.getTotalTimeNs());

        assertEquals(10, sample.getValue("first").getMean(), 0.1);
        assertEquals(50, sample.getValue("second").getMean(), 0.1);

        assertEquals(2_000, sample.getTimeMap().get("first").getIterations());
        assertEquals(500, sample.getTimeMap().get("second").getIterations());
    }

    @Test
    public void shouldCreateASpeedStatsUsingNormalDistribution() {
        SpeedStats stats = MockPerformanceCreator.speedStatsBuilder()
                .iterationsPerSample(100)
                .confidence(Ratio.decimal(0.1))
                .addTest("first")
                    .timeNs(10.0)
                    .stdev(5.0)
                    .samples(80)
                .endTest()
                .addTest("second")
                    .timeNs(20.0)
                    .stdev(7.0)
                    .samples(90)
                .endTest()
                .buildWithNormalDistribution();

        Measure firstMeasure = stats.getValue("first");
        Measure secondMeasure = stats.getValue("second");

        assertEquals(10.0, firstMeasure.getMean(), 2);
        assertEquals(20.0, secondMeasure.getMean(), 2);

        assertEquals(5.0, firstMeasure.getStandardDeviation(), 1);
        assertEquals(7.0, secondMeasure.getStandardDeviation(), 2);

        assertEquals(80, firstMeasure.getCount());
        assertEquals(90, secondMeasure.getCount());
    }

    @Test
    public void shouldCreateASpeedStatsUsingCoincidentalValues() {
        SpeedStats stats = MockPerformanceCreator.speedStatsBuilder()
                .iterationsPerSample(100)
                .confidence(Ratio.decimal(0.1))
                .addTest("first")
                    .timeNs(10.0)
                    .stdev(5.0)
                    .samples(80)
                .endTest()
                .addTest("second")
                    .timeNs(20.0)
                    .stdev(7.0)
                    .samples(90)
                .endTest()
                .buildWithCoincidentalValues();

        Measure firstMeasure = stats.getValue("first");
        Measure secondMeasure = stats.getValue("second");

        // values are coincidental
        assertEquals(10.0, firstMeasure.getMean(), 0);
        assertEquals(20.0, secondMeasure.getMean(), 0);

        // no standard deviation for coincidental values!
        assertEquals(0.0, firstMeasure.getStandardDeviation(), 0);
        assertEquals(0.0, secondMeasure.getStandardDeviation(), 0);

        assertEquals(80, firstMeasure.getCount());
        assertEquals(90, secondMeasure.getCount());
    }
}
