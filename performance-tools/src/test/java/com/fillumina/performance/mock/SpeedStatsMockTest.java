package com.fillumina.performance.mock;

import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.Ratio;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SpeedStatsMockTest {

    @Test
    public void shouldCreateASpeedStatsUsingNormalDistribution() {
        SpeedStats stats = SpeedStatsMock.builder()
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

        Measure firstMeasure = stats.getMeasure("first");
        Measure secondMeasure = stats.getMeasure("second");

        assertEquals(10.0, firstMeasure.getMean(), 2);
        assertEquals(20.0, secondMeasure.getMean(), 2);

        assertEquals(5.0, firstMeasure.getStandardDeviation(), 2);
        assertEquals(7.0, secondMeasure.getStandardDeviation(), 2);

        assertEquals(80, firstMeasure.getCount());
        assertEquals(90, secondMeasure.getCount());
    }

    @Test
    public void shouldCreateASpeedStatsUsingCoincidentalValues() {
        SpeedStats stats = SpeedStatsMock.builder()
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

        Measure firstMeasure = stats.getMeasure("first");
        Measure secondMeasure = stats.getMeasure("second");

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
