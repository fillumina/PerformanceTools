package com.fillumina.performance.assertion;

import com.fillumina.performance.FakePerformanceCreator;
import com.fillumina.performance.infrastructure.PHolder;
import com.fillumina.performance.speed.stats.AssertSpeed;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.util.stats.FakeMeasure;
import com.fillumina.performance.util.stats.MarginOfErrorConfidenceInterval;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.Ratio;
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
        final StatsAssertion<?,SpeedStats> ap =
                AssertSpeed.withTolerance(Ratio.percentage(1))
                    .assertOrder("First").lessThan("Second");

        final SpeedStats lp = FakePerformanceCreator
                .createCoincidentalStats(1_000, new Object[][] {
                    {"First", 33}, {"Second", 66}, {"Top", 100}
                });

        ap.check(new PHolder<>(null, lp));
    }

    @Test
    public void shouldNotBeFaster() {
        final StatsAssertion<?,SpeedStats> ap =
                AssertSpeed.withTolerance(Ratio.percentage(1.0))
                    .assertOrder("Second").lessThan("First");

        final SpeedStats stats = FakePerformanceCreator
                .createCoincidentalStats(1_000, new Object[][] {
                    {"First", 33}, {"Second", 66}, {"Top", 100}
                });

        try {
            ap.check(PHolder.createWithValue(stats));
        } catch (OrderAssertionError e) {
            assertEquals(OrderCondition.LESS, e.getRequiredCondition());
            assertEquals("Second", e.getFirstTestName());
            assertEquals("First", e.getSecondTestName());
            assertEquals(33, e.getSecondMeasure().getMean(), 1E-3);
            assertEquals(66, e.getFirstMeasure().getMean(), 1E-3);
            assertEquals(1.0, e.getTolerance().getPercentage(), 1E-3);
            return;
        }
        fail();
    }

    @Test
    public void shouldBeFasterWithHighTolerance() {
        final StatsAssertion<?,SpeedStats> highTolerance =
                AssertSpeed.withTolerance(Ratio.percentage(5))
                    .assertOrder("First").lessThan("Second");

        final SpeedStats stats = FakePerformanceCreator
                .createCoincidentalStats(1_000, new Object[][] {
                    {"First", 3710}, {"Second", 3700}
                });

        highTolerance.check(PHolder.createWithValue(stats));
    }

    @Test
    public void shouldNotBeFasterWithLowTolerance() {
        final StatsAssertion<?,SpeedStats> lowTolerance =
                AssertSpeed.withTolerance(Ratio.percentage(0.1))
                    .assertOrder("First").lessThan("Second");

        final SpeedStats stats = FakePerformanceCreator
                .createCoincidentalStats(1_000, new Object[][] {
                    {"First", 3710}, {"Second", 3700}
                });

        try {
            lowTolerance.check(PHolder.createWithValue(stats));
            fail();
        } catch (AssertionError e) {

        }
    }

    @Test
    public void shouldNotBeSlower() {
        final StatsAssertion<?,SpeedStats> ap =
                AssertSpeed.withTolerance(Ratio.percentage(1))
                    .assertOrder("First").greaterThan("Second");

        final SpeedStats lp = FakePerformanceCreator
                .createCoincidentalStats(1_000, new Object[][] {
                    {"First", 33}, {"Second", 66}, {"Top", 100}
                });

        try {
            ap.check(PHolder.createWithValue(lp));
        } catch (OrderAssertionError e) {
            assertEquals(OrderCondition.GREATER, e.getRequiredCondition());
            assertEquals("First", e.getFirstTestName());
            assertEquals("Second", e.getSecondTestName());
            assertEquals(33, e.getFirstMeasure().getMean(), 1E-3);
            assertEquals(66, e.getSecondMeasure().getMean(), 1E-3);
            assertEquals(1.0, e.getTolerance().getPercentage(), 1E-3);
            return;
        }
        fail();
    }

    @Test
    public void shouldNotBeEquals() {
        final StatsAssertion<?,SpeedStats> ap =
                AssertSpeed.withTolerance(Ratio.percentage(1))
                    .assertOrder("First").sameAs("Second");

        final SpeedStats stats = FakePerformanceCreator
                .createCoincidentalStats(1_000, new Object[][] {
                    {"First", 33}, {"Second", 66}, {"Top", 100}
                });

        try {
            ap.check(PHolder.createWithValue(stats));
            fail();
        } catch (OrderAssertionError e) {
            assertEquals(OrderCondition.SAME, e.getRequiredCondition());
            assertEquals("Second", e.getSecondTestName());
            assertEquals("First", e.getFirstTestName());
            assertEquals(66, e.getSecondMeasure().getMean(), 1E-3);
            assertEquals(33, e.getFirstMeasure().getMean(), 1E-3);
        }
    }

    @Test
    public void shouldReportNonExistentTest() {
        final StatsAssertion<?,SpeedStats> ap =
                AssertSpeed.withTolerance(Ratio.percentage(1))
                    .assertOrder("First").sameAs("NonExistent");

        final SpeedStats stats = FakePerformanceCreator
                .createCoincidentalStats(1_000, new Object[][] {
                    {"First", 33}, {"Second", 66}, {"Top", 100}
                });

        try {
            ap.check(PHolder.createWithValue(stats));
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Test 'NonExistent' not found, " +
                    "valid tests are: [First, Second, Top]",
                    e.getMessage());
        }
    }

    @Test
    public void shouldCheckTwoTestsSimultaneously() {
        final StatsAssertion<?,SpeedStats> ap =
                AssertSpeed.withTolerance(Ratio.percentage(1))
                    .assertOrder("First").lessThan("Second")
                    .assertOrder("Second").lessThan("Top");

        final SpeedStats stats = FakePerformanceCreator
                .createCoincidentalStats(1_000, new Object[][] {
                    {"First", 33}, {"Second", 66}, {"Top", 100}
                });

        try {
            ap.check(PHolder.createWithValue(stats));
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
        public double getMarginOfError(Ratio confidence) {
            return getStandardError() * StatFunctions.zeta(confidence.getValue());
        }

        @Override
        public MarginOfErrorConfidenceInterval getConfidenceInterval(
                Ratio confidence) {
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
                Ratio.percentage(10.0),
                OrderCondition.SAME);

        assertTrue(comply);
    }

    @Test
    public void shouldBeEqualWithConfidence0() {
        Measure firstMeasure =
                new MeasureImpl(5.34653740395317, 0.03210949319450669);
        Measure secondMeasure =
                new MeasureImpl(5.0513496559962086, 0.025385146660952432);

        boolean comply = AssertOrderCondition.comply(
                firstMeasure,
                secondMeasure,
                Ratio.percentage(7),
                OrderCondition.SAME);

        assertTrue(comply);
    }

    public static void main(final String[] args) {
        Measure firstMeasure =
                new MeasureImpl(5.34653740395317, 0.03210949319450669);

        System.out.println("0.95: " + firstMeasure.getConfidenceInterval(Ratio.P_95));
        System.out.println("0.05: " + firstMeasure.getConfidenceInterval(Ratio.value(0.05)));
        System.out.println("0.00: " + firstMeasure.getConfidenceInterval(Ratio.ZERO));
    }
}
