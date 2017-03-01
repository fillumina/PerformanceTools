package com.fillumina.performance.assertion;

import com.fillumina.performance.FakePerformanceCreator;
import com.fillumina.performance.infrastructure.PHolder;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.util.stats.Ratio;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati
 */
public class AssertPercentageTest {

    @Test
    public void shouldConfirmTheExpectedPercentages() {
        final StatsAssertion<?,SpeedStats> ap =
                AssertStats.<SpeedStats>withTolerance(Ratio.percentage(1))
            .assertPercentage("First").sameAs(33)
            .assertPercentage("Second").sameAs(66);

        final SpeedStats stats = FakePerformanceCreator
                .createCoincidentalStats(1_000, new Object[][] {
                    {"First", 33}, {"Second", 66}, {"Top", 100}
                });

        ap.check(PHolder.createWithValue(stats));
    }

    @Test
    public void shouldNotBeGreater() {
        final StatsAssertion<?,SpeedStats> ap =
                AssertStats.<SpeedStats>withTolerance(Ratio.percentage(1))
            .assertPercentage("First").greaterThan(50);

        final SpeedStats stats = FakePerformanceCreator
                .createCoincidentalStats(1_000, new Object[][] {
                    {"First", 33}, {"Second", 66}, {"Top", 100}
                });

        try {
            ap.check(PHolder.createWithValue(stats));
        } catch (PercentageAssertionError e) {
            assertEquals("First", e.getTestName());
            assertEquals(0.33, e.getRatio().getValue(), 1E-3);
            assertEquals(1.0, e.getTolerance().getPercentage(), 0);
            return;
        }
        fail();
    }

    @Test
    public void shouldNotBeLesser() {
        final StatsAssertion<?,SpeedStats> ap =
                AssertStats.<SpeedStats>withTolerance(Ratio.percentage(1))
            .assertPercentage("First").lessThan(10F);

        final SpeedStats stats = FakePerformanceCreator
                .createCoincidentalStats(1_000, new Object[][] {
                    {"First", 33}, {"Second", 66}, {"Top", 100}
                });

        try {
            ap.check(PHolder.createWithValue(stats));
        } catch (PercentageAssertionError e) {
            assertEquals("First", e.getTestName());
            assertEquals(0.33, e.getRatio().getValue(), 1E-3);
            assertEquals(1.0, e.getTolerance().getPercentage(), 0);
            return;
        }
        fail();
    }

    @Test
    public void shouldNotBeEquals() {
        final StatsAssertion<?,SpeedStats> ap =
                AssertStats.<SpeedStats>withTolerance(Ratio.percentage(1))
            .assertPercentage("First").sameAs(10F);

        final SpeedStats stats = FakePerformanceCreator
                .createCoincidentalStats(1_000, new Object[][] {
                    {"First", 33}, {"Second", 66}, {"Top", 100}
                });

        try {
            ap.check(PHolder.createWithValue(stats));
        } catch (PercentageAssertionError e) {
            assertEquals("First", e.getTestName());
            assertEquals(0.33, e.getRatio().getValue(), 1E-3);
            assertEquals(1.0, e.getTolerance().getPercentage(), 0);
            return;
        }
        fail();
    }
}
