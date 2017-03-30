package com.fillumina.performance.assertion;

import com.fillumina.performance.infrastructure.PHolder;
import com.fillumina.performance.mock.MockPerformanceCreator;
import com.fillumina.performance.speed.stats.AssertSpeed;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.util.stats.FakeMeasure;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.Ratio;
import java.util.Map;
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
                AssertSpeed.withTolerance(Ratio.ZERO)
                    .assertOrder("First").lessThan("Second");

        final SpeedStats stats = MockPerformanceCreator
                .createCoincidentalStats(new Object[][] {
                    {"First", 1_000, 33},
                    {"Second", 1_000, 66},
                    {"Top", 1_000, 100}
                });

        ap.check(new PHolder<>(null, stats));
    }

    @Test
    public void shouldNotBeFaster() {
        final StatsAssertion<?,SpeedStats> speedAssertion =
                AssertSpeed.withTolerance(Ratio.ZERO)
                    .assertOrder("Second").lessThan("First");

        final SpeedStats stats = MockPerformanceCreator
                .createCoincidentalStats(new Object[][] {
                    {"First", 1_000, 33},
                    {"Second", 1_000, 66},
                    {"Top", 1_000, 100}
                });

        try {
            speedAssertion.check(PHolder.createWithValue(stats));
        } catch (OrderAssertionError e) {
            assertEquals(EqCondition.LESS, e.getCondition());
            assertEquals("Second", e.getFirstTestName());
            assertEquals("First", e.getSecondTestName());
            assertEquals(33, e.getSecondMeasure().getMean(), 1E-3);
            assertEquals(66, e.getFirstMeasure().getMean(), 1E-3);
            assertEquals(0, e.getTolerance().getPercentage(), 1E-3);
            return;
        }
        fail();
    }

    @Test
    public void shouldBeFasterWithTolerance10() {
        final StatsAssertion<?,SpeedStats> highTolerance =
                AssertSpeed.withTolerance(Ratio.percentage(10))
                    .assertOrder("First").lessThan("Second");

        final SpeedStats stats = MockPerformanceCreator
                .createCoincidentalStats(new Object[][] {
                    {"First", 1_000, 109},
                    {"Second", 1_000, 100}
                });

        highTolerance.check(PHolder.createWithValue(stats));
    }

    @Test
    public void shouldNotBeFasterWithLowTolerance() {
        final StatsAssertion<?,SpeedStats> lowTolerance =
                AssertSpeed.withTolerance(Ratio.percentage(10))
                    .assertOrder("First").lessThan("Second");

        final SpeedStats stats = MockPerformanceCreator
                .createCoincidentalStats(new Object[][] {
                    {"First", 1_000, 110},
                    {"Second", 1_000, 100}
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
                AssertSpeed.withTolerance(Ratio.ZERO)
                    .assertOrder("First").greaterThan("Second");

        final SpeedStats lp = MockPerformanceCreator
                .createCoincidentalStats(new Object[][] {
                    {"First", 1_000, 33},
                    {"Second", 1_000, 66},
                    {"Top", 1_000, 100}
                });

        try {
            ap.check(PHolder.createWithValue(lp));
        } catch (OrderAssertionError e) {
            assertEquals(EqCondition.GREATER, e.getCondition());
            assertEquals("First", e.getFirstTestName());
            assertEquals("Second", e.getSecondTestName());
            assertEquals(33, e.getFirstMeasure().getMean(), 1E-3);
            assertEquals(66, e.getSecondMeasure().getMean(), 1E-3);
            assertEquals(0, e.getTolerance().getPercentage(), 1E-3);

            Map<EqCondition,ToleranceRequired> whatIfMap = e.getWhatIfToleranceMap();
            assertEquals(1.01, whatIfMap.get(EqCondition.GREATER).getDecimal(), 0);
            assertEquals(1.01, whatIfMap.get(EqCondition.EQUALS).getDecimal(), 0);
            assertNull(whatIfMap.get(EqCondition.LESS));
            return;
        }
        fail();
    }

    @Test
    public void shouldNotBeEquals() {
        final StatsAssertion<?,SpeedStats> ap =
                AssertSpeed.withTolerance(Ratio.ZERO)
                    .assertOrder("First").sameAs("Second");

        final SpeedStats stats = MockPerformanceCreator
                .createCoincidentalStats(new Object[][] {
                    {"First", 1_000, 33},
                    {"Second", 1_000, 66},
                    {"Top", 1_000, 100}
                });

        try {
            ap.check(PHolder.createWithValue(stats));
            fail();
        } catch (OrderAssertionError e) {
            assertEquals(EqCondition.EQUALS, e.getCondition());
            assertEquals("Second", e.getSecondTestName());
            assertEquals("First", e.getFirstTestName());
            assertEquals(66, e.getSecondMeasure().getMean(), 1E-3);
            assertEquals(33, e.getFirstMeasure().getMean(), 1E-3);
        }
    }

    @Test
    public void shouldReportNonExistentTest() {
        final StatsAssertion<?,SpeedStats> ap =
                AssertSpeed.withTolerance(Ratio.ZERO)
                    .assertOrder("First").sameAs("NonExistent");

        final SpeedStats stats = MockPerformanceCreator
                .createCoincidentalStats(new Object[][] {
                    {"First", 1_000, 33},
                    {"Second", 1_000, 66},
                    {"Top", 1_000, 100}
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
                AssertSpeed.withTolerance(Ratio.ZERO)
                    .assertOrder("First").lessThan("Second")
                    .assertOrder("Second").lessThan("Top");

        final SpeedStats stats = MockPerformanceCreator
                .createCoincidentalStats(new Object[][] {
                    {"First", 1_000, 33},
                    {"Second", 1_000, 66},
                    {"Top", 1_000, 100}
                });

        try {
            ap.check(PHolder.createWithValue(stats));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void shouldFailSecondTest() {
        final StatsAssertion<?,SpeedStats> ap =
                AssertSpeed.withTolerance(Ratio.ZERO)
                    .assertOrder("First").lessThan("Second")
                    .assertOrder("Second").lessThan("First");

        final SpeedStats stats = MockPerformanceCreator
                .createCoincidentalStats(new Object[][] {
                    {"First", 1_000, 33},
                    {"Second", 1_000, 66},
                    {"Top", 1_000, 100}
                });

        try {
            ap.check(PHolder.createWithValue(stats));
            fail("second test should fail");
        } catch (OrderAssertionError e) {
            assertEquals(EqCondition.LESS, e.getCondition());
            assertEquals("Second", e.getFirstTestName());
            assertEquals("First", e.getSecondTestName());
            assertEquals(66, e.getFirstMeasure().getMean(), 1E-3);
            assertEquals(33, e.getSecondMeasure().getMean(), 1E-3);
        }
    }

    private static class MeasureImpl extends FakeMeasure {
        private final double standardError;

        MeasureImpl(double mean, double standardError) {
            this.mean = mean;
            this.standardError = standardError;
        }

        @Override
        public double getStandardError() {
            return standardError;
        }
    }

    public static void main(final String[] args) {
        Measure firstMeasure =
                new MeasureImpl(5.34653740395317, 0.03210949319450669);

        System.out.println("0.95: " + firstMeasure.getConfidenceInterval(Ratio.P_95));
        System.out.println("0.05: " + firstMeasure.getConfidenceInterval(Ratio.decimal(0.05)));
        System.out.println("0.00: " + firstMeasure.getConfidenceInterval(Ratio.ZERO));
    }
}
