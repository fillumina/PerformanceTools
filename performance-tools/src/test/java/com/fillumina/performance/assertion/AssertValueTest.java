package com.fillumina.performance.assertion;

import com.fillumina.performance.mock.SpeedStatsMock;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.NormalDistributionMeasureBuilder;
import com.fillumina.performance.util.stats.Ratio;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AssertValueTest {

    @Test
    public void shouldConfirmTheExpectedPercentages() {
        final AssertStats<SpeedStats> ap =
                AssertStats.<SpeedStats>withTolerance(Ratio.ZERO)
            .assertValue("First").sameAs(33)
            .assertValue("Second").sameAs(66);

        final SpeedStats stats = SpeedStatsMock
                .builder()
                    .addTest("First").timeNs(33).endTest()
                    .addTest("Second").timeNs(66).endTest()
                    .addTest("Top").timeNs(100).endTest()
                .buildWithCoincidentalValues();

        ap.check(stats);
    }

    @Test
    public void shouldNotBeGreater() {
        final AssertStats<SpeedStats> ap =
                AssertStats.<SpeedStats>withTolerance(Ratio.ZERO)
            .assertValue("First").greaterThan(50);

        final SpeedStats stats = SpeedStatsMock
                .builder()
                    .addTest("First").timeNs(33).endTest()
                    .addTest("Second").timeNs(66).endTest()
                    .addTest("Top").timeNs(100).endTest()
                .buildWithCoincidentalValues();

        try {
            ap.check(stats);
        } catch (ValueAssertionError e) {
            assertEquals("First", e.getTestName().toString());
            assertEquals(33, e.getActualValue().getMean(), 1E-3);
            assertEquals(0, e.getTolerance().getPercentage(), 0);
            return;
        }
        fail();
    }

    @Test
    public void shouldNotBeLesser() {
        final AssertStats<SpeedStats> ap =
                AssertStats.<SpeedStats>withTolerance(Ratio.percentage(1))
            .assertValue("First").lessThan(10F);

        final SpeedStats stats = SpeedStatsMock
                .builder()
                    .addTest("First").timeNs(33).endTest()
                    .addTest("Second").timeNs(66).endTest()
                    .addTest("Top").timeNs(100).endTest()
                .buildWithCoincidentalValues();

        try {
            ap.check(stats);
        } catch (ValueAssertionError e) {
            assertEquals("First", e.getTestName().toString());
            assertEquals(33, e.getActualValue().getMean(), 1E-3);
            assertEquals(1.0, e.getTolerance().getPercentage(), 0);
            return;
        }
        fail();
    }

    @Test
    public void shouldNotBeEquals() {
        final AssertStats<SpeedStats> ap =
                AssertStats.<SpeedStats>withTolerance(Ratio.percentage(1))
            .assertValue("First").sameAs(10F);

        final SpeedStats stats = SpeedStatsMock
                .builder()
                    .addTest("First").timeNs(33).endTest()
                    .addTest("Second").timeNs(66).endTest()
                    .addTest("Top").timeNs(100).endTest()
                .buildWithCoincidentalValues();

        try {
            ap.check(stats);
        } catch (ValueAssertionError e) {
            assertEquals("First", e.getTestName().toString());
            assertEquals(33, e.getActualValue().getMean(), 1E-3);
            assertEquals(1.0, e.getTolerance().getPercentage(), 0);
            return;
        }
        fail();
    }

    public static void main(final String[] args) {
        Measure value = new NormalDistributionMeasureBuilder(10.0, 3.5, 0.1, 33)
                .build();

        for (double confidence = 0; confidence < 1; confidence += .1) {
            System.out.println("" + confidence + " -> " +
                    value.toStringForConfidence(Ratio.decimal(confidence)));
        }
    }
}
