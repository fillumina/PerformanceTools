package com.fillumina.performance.assertion;

import com.fillumina.performance.mock.SpeedStatsMock;
import com.fillumina.performance.time.stats.AverageTimeStats;
import com.fillumina.performance.time.stats.TimeStats;
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
        final AssertStats ap =
                AssertStats.withTolerance(Ratio.percentage(1))
            .assertPercentage("First").sameAs(33)
            .assertPercentage("Second").sameAs(66);

        final TimeStats stats = SpeedStatsMock
                .builder()
                    .addTest("First").timeNs(33).endTest()
                    .addTest("Second").timeNs(66).endTest()
                    .addTest("Top").timeNs(100).endTest()
                .buildWithCoincidentalValues(AverageTimeStats.class);

        ap.check(stats);
    }

    @Test
    public void shouldNotBeGreater() {
        final AssertStats ap =
                AssertStats.withTolerance(Ratio.percentage(1))
            .assertPercentage("First").greaterThan(50);

        final TimeStats stats = SpeedStatsMock
                .builder()
                    .addTest("First").timeNs(33).endTest()
                    .addTest("Second").timeNs(66).endTest()
                    .addTest("Top").timeNs(100).endTest()
                .buildWithCoincidentalValues(AverageTimeStats.class);

        try {
            ap.check(stats);
        } catch (PercentageAssertionError e) {
            assertEquals("First", e.getTestName().toString());
            assertEquals(0.33, e.getRatio().getValue(), 1E-3);
            assertEquals(1.0, e.getTolerance().getPercentage(), 0);
            return;
        }
        fail();
    }

    @Test
    public void shouldNotBeLesser() {
        final AssertStats ap =
                AssertStats.withTolerance(Ratio.percentage(1))
            .assertPercentage("First").lessThan(10F);

        final TimeStats stats = SpeedStatsMock
                .builder()
                    .addTest("First").timeNs(33).endTest()
                    .addTest("Second").timeNs(66).endTest()
                    .addTest("Top").timeNs(100).endTest()
                .buildWithCoincidentalValues(AverageTimeStats.class);

        try {
            ap.check(stats);
        } catch (PercentageAssertionError e) {
            assertEquals("First", e.getTestName().toString());
            assertEquals(0.33, e.getRatio().getValue(), 1E-3);
            assertEquals(1.0, e.getTolerance().getPercentage(), 0);
            return;
        }
        fail();
    }

    @Test
    public void shouldNotBeEquals() {
        final AssertStats ap =
                AssertStats.withTolerance(Ratio.percentage(1))
            .assertPercentage("First").sameAs(10F);

        final TimeStats stats = SpeedStatsMock
                .builder()
                    .addTest("First").timeNs(33).endTest()
                    .addTest("Second").timeNs(66).endTest()
                    .addTest("Top").timeNs(100).endTest()
                .buildWithCoincidentalValues(AverageTimeStats.class);

        try {
            ap.check(stats);
        } catch (PercentageAssertionError e) {
            assertEquals("First", e.getTestName().toString());
            assertEquals(0.33, e.getRatio().getValue(), 1E-3);
            assertEquals(1.0, e.getTolerance().getPercentage(), 0);
            return;
        }
        fail();
    }
}
