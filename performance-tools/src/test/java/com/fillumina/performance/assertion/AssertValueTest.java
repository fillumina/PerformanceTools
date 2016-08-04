package com.fillumina.performance.assertion;

import com.fillumina.performance.FakePerformanceCreator;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.NormalDistributionMeasureBuilder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AssertValueTest {

    @Test
    public void shouldConfirmTheExpectedPercentages() {
        final StatsAssertion<SpeedStats> ap =
                AssertPerformance.<SpeedStats>withTolerance(1)
            .assertValue("First").sameAs(33)
            .assertValue("Second").sameAs(66);

        final SpeedStats stats = FakePerformanceCreator
                .createCoincidentalStats(1_000, new Object[][] {
                    {"First", 33}, {"Second", 66}, {"Top", 100}
                });

        ap.check(stats);
    }

    @Test
    public void shouldNotBeGreater() {
        final StatsAssertion<SpeedStats> ap =
                AssertPerformance.<SpeedStats>withTolerance(1)
            .assertValue("First").greaterThan(50);

        final SpeedStats stats = FakePerformanceCreator
                .createCoincidentalStats(1_000, new Object[][] {
                    {"First", 33}, {"Second", 66}, {"Top", 100}
                });

        try {
            ap.check(stats);
        } catch (ValueAssertionError e) {
            assertEquals("First", e.getTestName());
            assertEquals(33, e.getActualValue().getMean(), 1E-3);
            assertEquals(1.0, e.getTolerance(), 0);
            return;
        }
        fail();
    }

    @Test
    public void shouldNotBeLesser() {
        final StatsAssertion<SpeedStats> ap =
                AssertPerformance.<SpeedStats>withTolerance(1F)
            .assertValue("First").lessThan(10F);

        final SpeedStats stats = FakePerformanceCreator
                .createCoincidentalStats(1_000, new Object[][] {
                    {"First", 33}, {"Second", 66}, {"Top", 100}
                });

        try {
            ap.check(stats);
        } catch (ValueAssertionError e) {
            assertEquals("First", e.getTestName());
            assertEquals(33, e.getActualValue().getMean(), 1E-3);
            assertEquals(1.0, e.getTolerance(), 0);
            return;
        }
        fail();
    }

    @Test
    public void shouldNotBeEquals() {
        final StatsAssertion<SpeedStats> ap =
                AssertPerformance.<SpeedStats>withTolerance(1F)
            .assertValue("First").sameAs(10F);

        final SpeedStats stats = FakePerformanceCreator
                .createCoincidentalStats(1_000, new Object[][] {
                    {"First", 33}, {"Second", 66}, {"Top", 100}
                });

        try {
            ap.check(stats);
        } catch (ValueAssertionError e) {
            assertEquals("First", e.getTestName());
            assertEquals(33, e.getActualValue().getMean(), 1E-3);
            assertEquals(1.0, e.getTolerance(), 0);
            return;
        }
        fail();
    }

    @Test
    public void shouldBeEqualsConsideringTolerance() {
        Measure value = new NormalDistributionMeasureBuilder(10.0, 1.5, 0.2, 33)
                .build();
        assertEquals(10.0, value.getMean(), 1);
        final boolean comply = AssertValueCondition.comply(value,
                        10.0,
                        10.0, // percentage points
                        EqualityCondition.SAME);
        assertTrue(comply);
    }

    public static void main(final String[] args) {
        Measure value = new NormalDistributionMeasureBuilder(10.0, 3.5, 0.1, 33)
                .build();

        for (double confidence = 0; confidence < 1; confidence += .1) {
            System.out.println("" + confidence + " -> " +
                    value.toStringForConfidence(confidence));
        }
    }
}
