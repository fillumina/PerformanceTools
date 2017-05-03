package com.fillumina.performance.assertion;

import com.fillumina.performance.infrastructure.TN;
import com.fillumina.performance.mock.SpeedStatsMock;
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

        final SpeedStats stats = SpeedStatsMock
                .builder()
                    .addTest("First").timeNs(33).endTest()
                    .addTest("Second").timeNs(66).endTest()
                    .addTest("Top").timeNs(100).endTest()
                .buildWithCoincidentalValues();

        ap.check(TN.EMPTY, stats);
    }

    @Test
    public void shouldNotBeFaster() {
        final StatsAssertion<?,SpeedStats> speedAssertion =
                AssertSpeed.withTolerance(Ratio.ZERO)
                    .assertOrder("Second").lessThan("First");

        final SpeedStats stats = SpeedStatsMock
                .builder()
                    .addTest("First").timeNs(33).endTest()
                    .addTest("Second").timeNs(66).endTest()
                    .addTest("Top").timeNs(100).endTest()
                .buildWithCoincidentalValues();

        try {
            speedAssertion.check(TN.EMPTY, stats);
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
        final StatsAssertion<?,SpeedStats> highTolerance =
                AssertSpeed.withTolerance(Ratio.percentage(10))
                    .assertOrder("First").lessThan("Second");

        final SpeedStats stats = SpeedStatsMock
                .builder()
                    .addTest("First").timeNs(109).endTest()
                    .addTest("Second").timeNs(100).endTest()
                .buildWithCoincidentalValues();

        highTolerance.check(TN.EMPTY, stats);
    }

    @Test
    public void shouldNotBeFasterWithLowTolerance() {
        final StatsAssertion<?,SpeedStats> lowTolerance =
                AssertSpeed.withTolerance(Ratio.percentage(10))
                    .assertOrder("First").lessThan("Second");

        final SpeedStats stats = SpeedStatsMock
                .builder()
                    .addTest("First").timeNs(110).endTest()
                    .addTest("Second").timeNs(100).endTest()
                .buildWithCoincidentalValues();

        try {
            lowTolerance.check(TN.EMPTY, stats);
            fail();
        } catch (AssertionError e) {

        }
    }

    @Test
    public void shouldNotBeSlower() {
        final StatsAssertion<?,SpeedStats> ap =
                AssertSpeed.withTolerance(Ratio.ZERO)
                    .assertOrder("First").greaterThan("Second");

        final SpeedStats lp = SpeedStatsMock
                .builder()
                    .addTest("First").timeNs(33).endTest()
                    .addTest("Second").timeNs(66).endTest()
                    .addTest("Top").timeNs(100).endTest()
                .buildWithCoincidentalValues();

        try {
            ap.check(TN.EMPTY, lp);
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
        final StatsAssertion<?,SpeedStats> ap =
                AssertSpeed.withTolerance(Ratio.ZERO)
                    .assertOrder("First").sameAs("Second");

        final SpeedStats stats = SpeedStatsMock
                .builder()
                    .addTest("First").timeNs(33).endTest()
                    .addTest("Second").timeNs(66).endTest()
                    .addTest("Top").timeNs(100).endTest()
                .buildWithCoincidentalValues();

        try {
            ap.check(TN.EMPTY, stats);
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
        final StatsAssertion<?,SpeedStats> ap =
                AssertSpeed.withTolerance(Ratio.ZERO)
                    .assertOrder("First").sameAs("NonExistent");

        final SpeedStats stats = SpeedStatsMock
                .builder()
                    .addTest("First").timeNs(33).endTest()
                    .addTest("Second").timeNs(66).endTest()
                    .addTest("Top").timeNs(100).endTest()
                .buildWithCoincidentalValues();

        try {
            ap.check(TN.EMPTY, stats);
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

        final SpeedStats stats = SpeedStatsMock
                .builder()
                    .addTest("First").timeNs(33).endTest()
                    .addTest("Second").timeNs(66).endTest()
                    .addTest("Top").timeNs(100).endTest()
                .buildWithCoincidentalValues();

        try {
            ap.check(TN.EMPTY, stats);
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

        final SpeedStats stats = SpeedStatsMock
                .builder()
                    .addTest("First").timeNs(33).endTest()
                    .addTest("Second").timeNs(66).endTest()
                    .addTest("Top").timeNs(100).endTest()
                .buildWithCoincidentalValues();

        try {
            ap.check(TN.EMPTY, stats);
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
