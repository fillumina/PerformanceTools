package com.fillumina.performance.executor.stats;

import com.fillumina.performance.assertion.MeasureNotFoundException;
import com.fillumina.performance.executor.TN;
import com.fillumina.performance.mock.MockStatsType;
import com.fillumina.performance.mock.StatsMockBuilder;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.Ratio;
import java.util.Arrays;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class StatsTest {

    @Test
    public void shouldGetStatistics() {
        Stats stats = new StatsMockBuilder()
                .confidence(Ratio.P_99)
                .addTest("first")
                    .mean(10.0)
                    .stdev(2.0)
                    .samples(200)
                .endTest()
                .addTest("second")
                    .mean(20.0)
                    .stdev(3.0)
                    .samples(250)
                .endTest()
                .buildWithNormalDistribution()
                .getStatsHolder(MockStatsType.INSTANCE)
                .getStats();

        final Measure first = stats.getMeasure("first");
        assertEquals(10.0, first.getMean(), 1.0);
        assertEquals(2.0, first.getStandardDeviation(), 0.5);
        assertTrue("count=" + first.getCount(),
                first.getCount() >= 200);

        final Measure second = stats.getMeasure("second");
        assertEquals(20.0, second.getMean(), 1.5);
        assertEquals(3.0, second.getStandardDeviation(), 0.5);
        assertTrue("count=" + second.getCount(),
                second.getCount() >= 250);
    }

    @Test
    public void shouldAnovaBe1IfMeasuresAreSignificant() {
        Stats stats = new StatsMockBuilder()
                .confidence(Ratio.decimal(0.9))
                .addTest("first").mean(10).stdev(5).samples(200).endTest()
                .addTest("second").mean(20).stdev(7).samples(250).endTest()
                .addTest("third").mean(30).stdev(5).samples(250).endTest()
                .buildWithNormalDistribution()
                .getStatsHolder(MockStatsType.INSTANCE)
                .getStats();

        assertEquals(
                StatsTableStringGenerator.INSTANCE.toString(stats),
                1.0, stats.getAnova(), 0.01);
    }

    @Test
    public void shouldReturnThePerformances() {
        Stats stats = new StatsMockBuilder()
                .confidence(Ratio.decimal(0.9))
                .addTest("first").mean(10).stdev(2).samples(250).endTest()
                .addTest("second").mean(20).stdev(4).samples(250).endTest()
                .addTest("third").mean(30).stdev(5).samples(250).endTest()
                .buildWithNormalDistribution()
                .getStatsHolder(MockStatsType.INSTANCE)
                .getStats();

        assertEquals(10.0, stats.getMeasure("first").getMean(), 1);
        assertEquals(20.0, stats.getMeasure("second").getMean(), 1);
        assertEquals(30.0, stats.getMeasure("third").getMean(), 1);
    }

    @Test
    public void shouldReturnTheMaximumPercentageMargin() {
        Stats stats = new StatsMockBuilder()
                .confidence(Ratio.decimal(0.9))
                .addTest("first").mean(10).stdev(8).samples(100).endTest()
                .addTest("second").mean(20).stdev(15).samples(100).endTest()
                .addTest("third").mean(30).stdev(20).samples(100).endTest()
                .buildWithNormalDistribution()
                .getStatsHolder(MockStatsType.INSTANCE)
                .getStats();

        final double max = stats.getMaximumPercentageMargin(Ratio.P_95)
                .getDecimal();
        assertTrue("max = " + max + System.lineSeparator() + stats.toString(),
                max > 0.01);
    }

    @Test(expected = MeasureNotFoundException.class)
    public void shouldThrowAnExceptionIfWrongName() {
        Stats stats = new StatsMockBuilder()
                .confidence(Ratio.decimal(0.9))
                .addTest("first").mean(10).stdev(25).samples(100).endTest()
                .addTest("second").mean(20).stdev(10).samples(100).endTest()
                .addTest("third").mean(30).stdev(5).samples(100).endTest()
                .buildWithNormalDistribution()
                .getStatsHolder(MockStatsType.INSTANCE)
                .getStats();

        stats.getMeasure("non existent");
    }

    @Test
    public void shouldAnovaBeLowWhenEquals() {
        Stats stats = new StatsMockBuilder()
                .confidence(Ratio.decimal(0.9))
                .addTest("first").mean(300).samples(100).endTest()
                .addTest("second").mean(300).samples(100).endTest()
                .buildWithCoincidentalValues()
                .getStatsHolder(MockStatsType.INSTANCE)
                .getStats();

        assertTrue(stats.toString(), stats.getAnova() < 0.6);
    }

    @Test
    public void shouldAnovaBeHightWhenDifferent() {
        Stats stats = new StatsMockBuilder()
                .confidence(Ratio.decimal(0.9))
                .addTest("first").mean(100).stdev(7.0).samples(100).endTest()
                .addTest("second").mean(50).stdev(7.0).samples(100).endTest()
                .buildWithNormalDistribution()
                .getStatsHolder(MockStatsType.INSTANCE)
                .getStats();

        assertTrue(stats.toString(), stats.getAnova() > 0.8);
    }

    @Test
    public void shouldCalculateRatioMatrix() {
        Stats stats = new StatsMockBuilder()
                .confidence(Ratio.decimal(0.9))
                .addTest("first").mean(10).stdev(5).samples(100).endTest()
                .addTest("second").mean(20).stdev(4).samples(100).endTest()
                .addTest("third").mean(30).stdev(5).samples(100).endTest()
                .buildWithNormalDistribution()
                .getStatsHolder(MockStatsType.INSTANCE)
                .getStats();

        assertEquals(10.0 / 20.0,
                stats.getRatio("first", "second", Ratio.P_95).getValue(),
                0.15);

        assertEquals(10.0 / 30.0,
                stats.getRatio("first", "third", Ratio.P_95).getValue(),
                0.15);

        assertEquals(20.0 / 30.0,
                stats.getRatio("second", "third", Ratio.P_95).getValue(),
                0.15);

        assertEquals(1.0,
                stats.getRatio("first", "first", Ratio.P_95).getValue(),
                0.15);

        assertEquals(1.0,
                stats.getRatio("second", "second", Ratio.P_95).getValue(),
                0.15);

        assertEquals(1.0,
                stats.getRatio("third", "third", Ratio.P_95).getValue(),
                0.15);
    }

    @Test
    public void shouldManageASingleTest() {
        Stats stats = new StatsMockBuilder()
                .confidence(Ratio.decimal(0.9))
                .addTest("single").mean(100).stdev(7.0).samples(100).endTest()
                .buildWithNormalDistribution()
                .getStatsHolder(MockStatsType.INSTANCE)
                .getStats();

        assertEquals(0, stats.getAnova(), 0.1);
        assertEquals(0,
                stats.getMaximumPercentageMargin(Ratio.P_95).getDecimal(),
                0.1);
        assertEquals(0.44, stats.getMinTukeyHsd(), 0.01);
        assertEquals(1.0,
                stats.getRatioWithRef("single", Ratio.P_95).getValue(),
                0.001);
        // 0.44 means equal
        assertEquals(0.44, stats.getTukeyHsd("single", "single"), 0.1);
        assertEquals(100, stats.getMeasure("single").getMean(), 2.0);
    }

    @Test
    public void shouldJoinTwoStats() {
        Stats a = new StatsMockBuilder()
                .confidence(Ratio.decimal(0.9))
                .addTest("one").mean(1.0).stdev(2.0).samples(33).endTest()
                .addTest("two").mean(2.0).stdev(2.0).samples(33).endTest()
                .buildWithSyntheticNormalValues()
                .getStatsHolder(MockStatsType.INSTANCE)
                .getStats();

        Stats b = new StatsMockBuilder()
                .confidence(Ratio.decimal(0.9))
                .addTest("three").mean(3.0).stdev(2.0).samples(33).endTest()
                .addTest("four").mean(4.0).stdev(2.0).samples(33).endTest()
                .buildWithSyntheticNormalValues()
                .getStatsHolder(MockStatsType.INSTANCE)
                .getStats();

        Stats sum = a.join(b);

        assertEquals(Arrays.asList(
                TN.tname("one"),
                TN.tname("two"),
                TN.tname("three"),
                TN.tname("four")), sum.getNames());

        assertEquals(1.0, sum.getMeasure("one").getMean(), 0.1);
        assertEquals(2.0, sum.getMeasure("two").getMean(), 0.1);
        assertEquals(3.0, sum.getMeasure("three").getMean(), 0.1);
        assertEquals(4.0, sum.getMeasure("four").getMean(), 0.1);
    }
}
