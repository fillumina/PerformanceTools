package com.fillumina.performance.assertion;

import com.fillumina.performance.mock.SpeedStatsMock;
import com.fillumina.performance.time.AssertTime;
import com.fillumina.performance.time.stats.AverageTimeStats;
import com.fillumina.performance.time.stats.TimeStats;
import com.fillumina.performance.util.EqCondition;
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
        final AssertStats<TimeStats> ap =
                AssertTime.withTolerance(Ratio.ZERO)
                    .assertOrder("First").lessThan("Second");

        final TimeStats stats = SpeedStatsMock
                .builder()
                    .addTest("First").timeNs(33).endTest()
                    .addTest("Second").timeNs(66).endTest()
                    .addTest("Top").timeNs(100).endTest()
                .buildWithCoincidentalValues(AverageTimeStats.class);

        ap.check(stats);
    }

    @Test
    public void shouldNotBeFaster() {
        final AssertStats<TimeStats> speedAssertion =
                AssertTime.withTolerance(Ratio.ZERO)
                    .assertOrder("Second").lessThan("First");

        final TimeStats stats = SpeedStatsMock
                .builder()
                    .addTest("First").timeNs(33).endTest()
                    .addTest("Second").timeNs(66).endTest()
                    .addTest("Top").timeNs(100).endTest()
                .buildWithCoincidentalValues(AverageTimeStats.class);

        try {
            speedAssertion.check(stats);
        } catch (OrderAssertionError e) {
            assertEquals(EqCondition.LESS, e.getCondition());
            assertEquals("Second", e.getFirstTestName().toString());
            assertEquals("First", e.getSecondTestName().toString());
            assertEquals(33, e.getSecondMeasure().getMean(), 1E-3);
            assertEquals(66, e.getFirstMeasure().getMean(), 1E-3);
            assertEquals(0, e.getTolerance().getPercentage(), 1E-3);
            return;
        }
        fail();
    }

    @Test
    public void shouldBeFasterWithTolerance10() {
        final AssertStats<TimeStats> highTolerance =
                AssertTime.withTolerance(Ratio.percentage(10))
                    .assertOrder("First").lessThan("Second");

        final TimeStats stats = SpeedStatsMock
                .builder()
                    .addTest("First").timeNs(109).endTest()
                    .addTest("Second").timeNs(100).endTest()
                .buildWithCoincidentalValues(AverageTimeStats.class);

        highTolerance.check(stats);
    }

    @Test
    public void shouldNotBeFasterWithLowTolerance() {
        final AssertStats<TimeStats> lowTolerance =
                AssertTime.withTolerance(Ratio.percentage(10))
                    .assertOrder("First").lessThan("Second");

        final TimeStats stats = SpeedStatsMock
                .builder()
                    .addTest("First").timeNs(110).endTest()
                    .addTest("Second").timeNs(100).endTest()
                .buildWithCoincidentalValues(AverageTimeStats.class);

        try {
            lowTolerance.check(stats);
            fail();
        } catch (AssertionError e) {

        }
    }

    @Test
    public void shouldNotBeSlower() {
        final AssertStats<TimeStats> ap =
                AssertTime.withTolerance(Ratio.ZERO)
                    .assertOrder("First").greaterThan("Second");

        final TimeStats lp = SpeedStatsMock
                .builder()
                    .addTest("First").timeNs(33).endTest()
                    .addTest("Second").timeNs(66).endTest()
                    .addTest("Top").timeNs(100).endTest()
                .buildWithCoincidentalValues(AverageTimeStats.class);

        try {
            ap.check(lp);
        } catch (OrderAssertionError e) {
            assertEquals(EqCondition.GREATER, e.getCondition());
            assertEquals("First", e.getFirstTestName().toString());
            assertEquals("Second", e.getSecondTestName().toString());
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
        final AssertStats<TimeStats> ap =
                AssertTime.withTolerance(Ratio.ZERO)
                    .assertOrder("First").sameAs("Second");

        final TimeStats stats = SpeedStatsMock
                .builder()
                    .addTest("First").timeNs(33).endTest()
                    .addTest("Second").timeNs(66).endTest()
                    .addTest("Top").timeNs(100).endTest()
                .buildWithCoincidentalValues(AverageTimeStats.class);

        try {
            ap.check(stats);
            fail();
        } catch (OrderAssertionError e) {
            assertEquals(EqCondition.EQUALS, e.getCondition());
            assertEquals("Second", e.getSecondTestName().toString());
            assertEquals("First", e.getFirstTestName().toString());
            assertEquals(66, e.getSecondMeasure().getMean(), 1E-3);
            assertEquals(33, e.getFirstMeasure().getMean(), 1E-3);
        }
    }

    @Test
    public void shouldReportNonExistentTest() {
        final AssertStats<TimeStats> ap =
                AssertTime.withTolerance(Ratio.ZERO)
                    .assertOrder("First").sameAs("NonExistent");

        final TimeStats stats = SpeedStatsMock
                .builder()
                    .addTest("First").timeNs(33).endTest()
                    .addTest("Second").timeNs(66).endTest()
                    .addTest("Top").timeNs(100).endTest()
                .buildWithCoincidentalValues(AverageTimeStats.class);

        try {
            ap.check(stats);
            fail();
        } catch (TestNotFoundException e) {
            assertEquals("test 'NonExistent' not found, " +
                    "valid tests are: [First, Second, Top]",
                    e.getMessage());
        }
    }

    @Test
    public void shouldCheckTwoTestsSimultaneously() {
        final AssertStats<TimeStats> ap =
                AssertTime.withTolerance(Ratio.ZERO)
                    .assertOrder("First").lessThan("Second")
                    .assertOrder("Second").lessThan("Top");

        final TimeStats stats = SpeedStatsMock
                .builder()
                    .addTest("First").timeNs(33).endTest()
                    .addTest("Second").timeNs(66).endTest()
                    .addTest("Top").timeNs(100).endTest()
                .buildWithCoincidentalValues(AverageTimeStats.class);

        try {
            ap.check(stats);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void shouldFailSecondTest() {
        final AssertStats<TimeStats> ap =
                AssertTime.withTolerance(Ratio.ZERO)
                    .assertOrder("First").lessThan("Second")
                    .assertOrder("Second").lessThan("First");

        final TimeStats stats = SpeedStatsMock
                .builder()
                    .addTest("First").timeNs(33).endTest()
                    .addTest("Second").timeNs(66).endTest()
                    .addTest("Top").timeNs(100).endTest()
                .buildWithCoincidentalValues(AverageTimeStats.class);

        try {
            ap.check(stats);
            fail("second test should fail");
        } catch (OrderAssertionError e) {
            assertEquals(EqCondition.LESS, e.getCondition());
            assertEquals("Second", e.getFirstTestName().toString());
            assertEquals("First", e.getSecondTestName().toString());
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
