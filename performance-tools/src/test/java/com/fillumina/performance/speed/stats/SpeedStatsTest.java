package com.fillumina.performance.speed.stats;

import com.fillumina.performance.mock.MockPerformanceCreator;
import com.fillumina.performance.speed.stats.strgen.SpeedStatsTableStringGenerator;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.Ratio;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SpeedStatsTest {

    @Test
    public void shouldGetStatistics() {
        SpeedStats stats = MockPerformanceCreator.speedStatsBuilder()
                .iterationsPerSample(100)
                .confidence(Ratio.P_99)
                .addTest("first")
                    .timeNs(10.0)
                    .stdev(2.0)
                    .samples(200)
                .endTest()
                .addTest("second")
                    .timeNs(20.0)
                    .stdev(3.0)
                    .samples(250)
                .endTest()
                .buildWithNormalDistribution();

        final Measure first = stats.getValue("first");
        assertEquals(10.0, first.getMean(), 1.0);
        assertEquals(2.0, first.getStandardDeviation(), 0.5);
        assertTrue("count=" + first.getCount(),
                first.getCount() >= 200);

        final Measure second = stats.getValue("second");
        assertEquals(20.0, second.getMean(), 1.5);
        assertEquals(3.0, second.getStandardDeviation(), 0.1);
        assertTrue("count=" + second.getCount(),
                second.getCount() >= 250);
    }

    @Test
    public void shouldAnovaBe1IfMeasuresAreSignificant() {
        SpeedStats stats = MockPerformanceCreator
                .speedStatsBuilder()
                .confidence(Ratio.decimal(0.9))
                .iterationsPerSample(300)
                .addTest("first").timeNs(10).stdev(5).samples(200).endTest()
                .addTest("second").timeNs(20).stdev(7).samples(250).endTest()
                .addTest("third").timeNs(30).stdev(5).samples(250).endTest()
                .buildWithNormalDistribution();

        assertEquals(SpeedStatsTableStringGenerator.INSTANCE.toString(stats),
                1.0, stats.getAnova(), 0.01);
    }

    @Test
    public void shouldReturnThePerformances() {
        SpeedStats stats = MockPerformanceCreator
                .speedStatsBuilder()
                .confidence(Ratio.decimal(0.9))
                .iterationsPerSample(300)
                .addTest("first").timeNs(10).stdev(2).samples(250).endTest()
                .addTest("second").timeNs(20).stdev(4).samples(250).endTest()
                .addTest("third").timeNs(30).stdev(5).samples(250).endTest()
                .buildWithNormalDistribution();

        assertEquals(10.0, stats.getValue("first").getMean(), 1);
        assertEquals(20.0, stats.getValue("second").getMean(), 1);
        assertEquals(30.0, stats.getValue("third").getMean(), 1);
    }

    @Test
    public void shouldAccountTheTotalTime() {
        SpeedStats stats = MockPerformanceCreator
                .speedStatsBuilder()
                .confidence(Ratio.decimal(0.9))
                .iterationsPerSample(100)
                .addTest("first").timeNs(10).stdev(5).samples(100).endTest()
                .addTest("second").timeNs(20).stdev(4).samples(100).endTest()
                .addTest("third").timeNs(30).stdev(5).samples(100).endTest()
                .buildWithNormalDistribution();

        final double expectedTotalTime =
                10.0 * stats.getValue("first").getCount() * 100 +
                20.0 * stats.getValue("second").getCount() * 100 +
                30.0 * stats.getValue("third").getCount() * 100;

        assertEquals(expectedTotalTime, stats.getTotalTimeNs(), 100_000 );
    }

    @Test
    public void shouldReturnTheMaximumPercentageMargin() {
        SpeedStats stats = MockPerformanceCreator
                .speedStatsBuilder()
                .confidence(Ratio.decimal(0.9))
                .iterationsPerSample(300)
                .addTest("first").timeNs(10).stdev(8).samples(100).endTest()
                .addTest("second").timeNs(20).stdev(15).samples(100).endTest()
                .addTest("third").timeNs(30).stdev(20).samples(100).endTest()
                .buildWithNormalDistribution();

        final double max = stats.getMaximumPercentageMargin();
        assertTrue("max = " + max + System.lineSeparator() + stats.toString(),
                max > 0.01);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowAnExceptionIfWrongName() {
        SpeedStats stats = MockPerformanceCreator
                .speedStatsBuilder()
                .confidence(Ratio.decimal(0.9))
                .iterationsPerSample(300)
                .addTest("first").timeNs(10).stdev(25).samples(100).endTest()
                .addTest("second").timeNs(20).stdev(10).samples(100).endTest()
                .addTest("third").timeNs(30).stdev(5).samples(100).endTest()
                .buildWithNormalDistribution();

        stats.getValue("non existent");
    }

    @Test
    public void shouldAnovaBeLowWhenEquals() {
        SpeedStats stats = MockPerformanceCreator.speedStatsBuilder()
                .iterationsPerSample(300)
                .confidence(Ratio.decimal(0.9))
                .addTest("first").timeNs(300).samples(100).endTest()
                .addTest("second").timeNs(300).samples(100).endTest()
                .buildWithCoincidentalValues();

        assertTrue(stats.toString(), stats.getAnova() < 0.9);
    }

    @Test
    public void shouldAnovaBeHightWhenDifferent() {
        SpeedStats stats = MockPerformanceCreator.speedStatsBuilder()
                .iterationsPerSample(300)
                .confidence(Ratio.decimal(0.9))
                .addTest("first").timeNs(100).stdev(7.0).samples(100).endTest()
                .addTest("second").timeNs(50).stdev(7.0).samples(100).endTest()
                .buildWithNormalDistribution();

        assertTrue(stats.toString(), stats.getAnova() > 0.8);
    }
}
