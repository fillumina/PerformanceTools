package com.fillumina.performance.stats;

import com.fillumina.performance.FakePerformanceCreator;
import com.fillumina.performance.util.stats.Measure;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class PerformanceStatsTest {

    @Test
    public void shouldGetStatistics() {
        PerformanceStats stats = FakePerformanceCreator
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
        PerformanceStats stats = FakePerformanceCreator
                .createPerformanceStats(100, new Object[][] {
            {"first", 10.0, 5.0, 200},
            {"second", 20.0, 7.0, 250},
            {"third", 10.0, 5.0, 250}
        });
        assertEquals(1.0, stats.getAnova(), 0.01);
    }

    @Test
    public void shouldAnovaBe0IfNotSignificantMeasures() {
        PerformanceStats stats = FakePerformanceCreator
                .createPerformanceStats(100, new Object[][] {
            {"first", 10.0, 80.0, 200},
            {"second", 10.0, 65.0, 250},
            {"third", 10.0, 50.0, 250}
        });
        final double anova = stats.getAnova();
        assertTrue("anova = " + anova, anova < 0.5);
    }

    @Test
    public void shouldReturnThePerformances() {
        PerformanceStats stats = FakePerformanceCreator
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
        PerformanceStats stats = FakePerformanceCreator
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
}
