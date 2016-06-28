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
                .createPerformanceStats(100, new Object[][] {
            {"first", 10.0, 5.0, 200},
            {"second", 20.0, 7.0, 250}
        });

        final Measure first = stats.getPerformance("first");
        assertEquals(10.0, first.getMean(), 0.5);
        assertEquals(5.0, first.getStandardDeviation(), 0.1);
        assertTrue(first.getCount() >= 200);

        final Measure second = stats.getPerformance("second");
        assertEquals(20.0, second.getMean(), 0.5);
        assertEquals(7.0, second.getStandardDeviation(), 0.1);
        assertTrue(second.getCount() >= 250);
    }

    @Test
    public void shouldAnovaBe1IfSignificangMeasures() {
        SpeedStats stats = FakePerformanceCreator
                .createPerformanceStats(100, new Object[][] {
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
                .createPerformanceStats(100, new Object[][] {
            {"first", 100.0, 80.0, 200},
            {"second", 100.0, 65.0, 250},
            {"third", 100.0, 70.0, 250}
        });
        final double anova = stats.getAnova();
        assertTrue("anova = " + anova +
                "\n" +  SpeedTableStringGenerator.INSTANCE.toString(stats),
                anova < 0.85);
    }

    @Test
    public void shouldReturnThePerformances() {
        SpeedStats stats = FakePerformanceCreator
                .createPerformanceStats(100, new Object[][] {
            {"first", 10.0, 5.0, 200},
            {"second", 20.0, 4.0, 250},
            {"third", 30.0, 5.0, 250}
        });
        assertEquals(10.0, stats.getPerformance("first").getMean(), 0.5);
        assertEquals(20.0, stats.getPerformance("second").getMean(), 0.5);
        assertEquals(30.0, stats.getPerformance("third").getMean(), 0.5);
    }

    @Test
    public void shouldAccountTheTotalTime() {
        SpeedStats stats = FakePerformanceCreator
                .createPerformanceStats(300, new Object[][] {
            {"first", 10.0, 5.0, 100},
            {"second", 20.0, 4.0, 100},
            {"third", 30.0, 5.0, 100}
        });
        final double expectedTotalTime =
                10.0 * stats.getPerformance("first").getCount() * 300 +
                20.0 * stats.getPerformance("second").getCount() * 300 +
                30.0 * stats.getPerformance("third").getCount() * 300;
        assertTrue(expectedTotalTime * 1.0 / stats.getTotalTime() > 0.99 );
    }

    @Test
    public void shouldReturnTheMaximumPercentageMargin() {
        SpeedStats stats = FakePerformanceCreator
                .createPerformanceStats(300, new Object[][] {
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
                .createPerformanceStats(300, new Object[][] {
            {"first", 10.0, 25.0, 100},
            {"second", 20.0, 10.0, 100},
            {"third", 30.0, 5.0, 100}
        });
        stats.getPerformance("non existent");
    }
}
