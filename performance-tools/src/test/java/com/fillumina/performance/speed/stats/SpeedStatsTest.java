package com.fillumina.performance.speed.stats;

import com.fillumina.performance.FakePerformanceCreator;
import com.fillumina.performance.speed.stats.strgen.SpeedTableStringGenerator;
import com.fillumina.performance.util.stats.Measure;
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
        SpeedStats stats = FakePerformanceCreator
                .createPerformanceStats(100, 0.01, new Object[][] {
            {"first", 10.0, 2.0, 200},
            {"second", 20.0, 3.0, 250}
        });

        final Measure first = stats.getPerformance("first");
        assertEquals(10.0, first.getMean(), 1.0);
        assertEquals(2.0, first.getStandardDeviation(), 0.5);
        assertTrue("count=" + first.getCount(),
                first.getCount() >= 200);

        final Measure second = stats.getPerformance("second");
        assertEquals(20.0, second.getMean(), 1.5);
        assertEquals(3.0, second.getStandardDeviation(), 0.1);
        assertTrue("count=" + second.getCount(),
                second.getCount() >= 250);
    }

    @Test
    public void shouldAnovaBe1IfSignificangMeasures() {
        SpeedStats stats = FakePerformanceCreator
                .createPerformanceStats(100, 0.1, new Object[][] {
            {"first", 10.0, 5.0, 200},
            {"second", 20.0, 7.0, 250},
            {"third", 10.0, 5.0, 250}
        });
        assertEquals(SpeedTableStringGenerator.INSTANCE.toString(stats),
                1.0, stats.getAnova(), 0.01);
    }

    @Test
    public void shouldAnovaBe0IfNotSignificantMeasures() {
        SpeedStats stats = FakePerformanceCreator
                .createPerformanceStats(100, 0.6, new Object[][] {
            {"first", 100.0, 80.0, 33},
            {"second", 100.0, 65.0, 33},
            {"third", 100.0, 70.0, 33}
        });
        final double anova = stats.getAnova();
        assertTrue("anova = " + anova +
                "\n" +  SpeedTableStringGenerator.INSTANCE.toString(stats),
                anova < 0.90);
    }

    @Test
    public void shouldReturnThePerformances() {
        SpeedStats stats = FakePerformanceCreator
                .createPerformanceStats(100, 0.01, new Object[][] {
            {"first", 10.0, 2.0, 200},
            {"second", 20.0, 4.0, 250},
            {"third", 30.0, 5.0, 250}
        });
        assertEquals(10.0, stats.getPerformance("first").getMean(), 1);
        assertEquals(20.0, stats.getPerformance("second").getMean(), 1);
        assertEquals(30.0, stats.getPerformance("third").getMean(), 1);
    }

    @Test
    public void shouldAccountTheTotalTime() {
        SpeedStats stats = FakePerformanceCreator
                .createPerformanceStats(100, 0.1, new Object[][] {
            {"first", 10.0, 5.0, 100},
            {"second", 20.0, 4.0, 100},
            {"third", 30.0, 5.0, 100}
        });
        final double expectedTotalTime =
                10.0 * stats.getPerformance("first").getCount() * 100 +
                20.0 * stats.getPerformance("second").getCount() * 100 +
                30.0 * stats.getPerformance("third").getCount() * 100;
        assertEquals(expectedTotalTime, stats.getTotalTime(), 100_000 );
    }

    @Test
    public void shouldReturnTheMaximumPercentageMargin() {
        SpeedStats stats = FakePerformanceCreator
                .createPerformanceStats(300, 0.1, new Object[][] {
            {"first", 10.0, 8.0, 100},
            {"second", 20.0, 15.0, 100},
            {"third", 30.0, 20.0, 100}
        });
        final double max = stats.getMaximumPercentageMargin();
        assertTrue("max = " + max + "\n" + stats.toString(), max > 0.01);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowAnExceptionIfWrongName() {
        SpeedStats stats = FakePerformanceCreator
                .createPerformanceStats(300, 0.1, new Object[][] {
            {"first", 10.0, 25.0, 100},
            {"second", 20.0, 10.0, 100},
            {"third", 30.0, 5.0, 100}
        });
        stats.getPerformance("non existent");
    }
}
