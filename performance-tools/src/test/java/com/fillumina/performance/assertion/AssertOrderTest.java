package com.fillumina.performance.assertion;

import com.fillumina.performance.FakePerformanceCreator;
import com.fillumina.performance.speed.stats.AssertSpeed;
import com.fillumina.performance.speed.stats.PerformanceStats;
import com.fillumina.performance.util.stats.FakeMeasure;
import com.fillumina.performance.util.stats.MarginOfErrorConfidenceInterval;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.StatFunctions;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati
 */
public class AssertOrderTest {

    @Test
    public void shouldConfirmTheExpectedOrder() {
        final StatsAssertion<PerformanceStats> ap = AssertSpeed.withTolerance(1F)
            .assertOrder("First").fasterThan("Second");

        final PerformanceStats lp = FakePerformanceCreator
                .createCoincidentalStats(1_000, new Object[][] {
                    {"First", 33}, {"Second", 66}, {"Top", 100}
                });

        ap.check(lp);
    }

    @Test
    public void shouldNotBeFaster() {
        final StatsAssertion<PerformanceStats> ap = AssertSpeed.withTolerance(1F)
            .assertOrder("Second").fasterThan("First");

        final PerformanceStats stats = FakePerformanceCreator
                .createCoincidentalStats(1_000, new Object[][] {
                    {"First", 33}, {"Second", 66}, {"Top", 100}
                });

        try {
            ap.check(stats);
        } catch (OrderAssertionError e) {
            assertEquals(OrderCondition.FASTER, e.getRequiredCondition());
            assertEquals("Second", e.getFirstTestName());
            assertEquals("First", e.getSecondTestName());
            assertEquals(0.033, e.getSecondMeasure().getMean(), 1E-3);
            assertEquals(0.066, e.getFirstMeasure().getMean(), 1E-3);
            assertEquals(1.0, e.getTolerance(), 1E-3);
            return;
        }
        fail();
    }

    @Test
    public void shouldBeFasterWithHighTolerance() {
        final StatsAssertion<PerformanceStats> highTolerance =
                AssertSpeed.withTolerance(5)
                    .assertOrder("First").fasterThan("Second");

        final PerformanceStats stats = FakePerformanceCreator
                .createCoincidentalStats(1_000, new Object[][] {
                    {"First", 3710}, {"Second", 3700}
                });

        highTolerance.check(stats);
    }

    @Test
    public void shouldNotBeFasterWithLowTolerance() {
        final StatsAssertion<PerformanceStats> lowTolerance =
                AssertSpeed.withTolerance(0.1)
                    .assertOrder("First").fasterThan("Second");

        final PerformanceStats stats = FakePerformanceCreator
                .createCoincidentalStats(1_000, new Object[][] {
                    {"First", 3710}, {"Second", 3700}
                });

        try {
            lowTolerance.check(stats);
            fail();
        } catch (AssertionError e) {

        }
    }

    @Test
    public void shouldNotBeSlower() {
        final StatsAssertion<PerformanceStats> ap = AssertSpeed.withTolerance(1F)
            .assertOrder("First").slowerThan("Second");

        final PerformanceStats lp = FakePerformanceCreator
                .createCoincidentalStats(1_000, new Object[][] {
                    {"First", 33}, {"Second", 66}, {"Top", 100}
                });

        try {
            ap.check(lp);
        } catch (OrderAssertionError e) {
            assertEquals(OrderCondition.SLOWER, e.getRequiredCondition());
            assertEquals("First", e.getFirstTestName());
            assertEquals("Second", e.getSecondTestName());
            assertEquals(0.033, e.getFirstMeasure().getMean(), 1E-3);
            assertEquals(0.066, e.getSecondMeasure().getMean(), 1E-3);
            assertEquals(1.0, e.getTolerance(), 1E-3);
            return;
        }
        fail();
    }

    @Test
    public void shouldNotBeEquals() {
        final StatsAssertion<PerformanceStats> ap = AssertSpeed.withTolerance(1F)
            .assertOrder("First").sameAs("Second");

        final PerformanceStats stats = FakePerformanceCreator
                .createCoincidentalStats(1_000, new Object[][] {
                    {"First", 33}, {"Second", 66}, {"Top", 100}
                });

        try {
            ap.check(stats);
            fail();
        } catch (OrderAssertionError e) {
            assertEquals(OrderCondition.SAME, e.getRequiredCondition());
            assertEquals("Second", e.getSecondTestName());
            assertEquals("First", e.getFirstTestName());
            assertEquals(0.066, e.getSecondMeasure().getMean(), 1E-3);
            assertEquals(0.033, e.getFirstMeasure().getMean(), 1E-3);
        }
    }

    @Test
    public void shouldReportNonExistentTest() {
        final StatsAssertion<PerformanceStats> ap = AssertSpeed.withTolerance(1F)
            .assertOrder("First").sameAs("NonExistent");

        final PerformanceStats stats = FakePerformanceCreator
                .createCoincidentalStats(1_000, new Object[][] {
                    {"First", 33}, {"Second", 66}, {"Top", 100}
                });

        try {
            ap.check(stats);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Test 'NonExistent' not found, " +
                    "valid tests are: [First, Second, Top]",
                    e.getMessage());
        }
    }

    @Test
    public void shouldCheckTwoTestsSimultaneously() {
        final StatsAssertion<PerformanceStats> ap = AssertSpeed.withTolerance(1F)
            .assertOrder("First").fasterThan("Second")
            .assertOrder("Second").fasterThan("Top");

        final PerformanceStats stats = FakePerformanceCreator
                .createCoincidentalStats(1_000, new Object[][] {
                    {"First", 33}, {"Second", 66}, {"Top", 100}
                });

        try {
            ap.check(stats);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    private static class MeasureImpl extends FakeMeasure {
        MeasureImpl(double mean, double standardError) {
            this.mean = mean;
            this.standardError = standardError;
        }

        @Override
        public double getMarginOfError(double confidence) {
            return getStandardError() * StatFunctions.zeta(confidence);
        }

        @Override
        public MarginOfErrorConfidenceInterval getConfidenceInterval(
                double confidence) {
            return new MarginOfErrorConfidenceInterval(mean,
                    getMarginOfError(confidence), confidence);
        }
    }

    @Test
    public void shouldBeEqualConsideringTolerance() {
        Measure firstMeasure =
                new MeasureImpl(5.34653740395317, 0.03210949319450669);
        Measure secondMeasure =
                new MeasureImpl(5.0513496559962086, 0.025385146660952432);

        boolean comply = AssertOrderCondition.comply(
                firstMeasure,
                secondMeasure,
                5.0,
                OrderCondition.SAME);

        assertTrue(comply);
    }
}
