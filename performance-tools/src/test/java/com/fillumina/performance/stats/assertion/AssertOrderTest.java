package com.fillumina.performance.stats.assertion;

import com.fillumina.performance.stats.FakePerformanceCreator;
import com.fillumina.performance.stats.PerformanceStats;
import com.fillumina.performance.stats.assertion.AssertOrder.AssertOrderCondition;
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
        final PerformanceAssertion ap = AssertPerformance.withTolerance(1F)
            .assertTest("First").fasterThan("Second");

        final PerformanceStats lp = FakePerformanceCreator.createStats(1_000,
                new Object[][] {
                    {"First", 33}, {"Second", 66}, {"Top", 100}
                });

        ap.check(lp);
    }

    @Test
    public void shouldNotBeFaster() {
        final PerformanceAssertion ap = AssertPerformance.withTolerance(1F)
            .assertTest("Second").fasterThan("First");

        final PerformanceStats stats = FakePerformanceCreator.createStats(1_000,
                new Object[][] {
                    {"First", 33}, {"Second", 66}, {"Top", 100}
                });

        try {
            ap.check(stats);
        } catch (OrderAssertionError e) {
            assertEquals(OrderCondition.FASTER, e.getRequiredCondition());
            assertEquals("Second", e.getFirstTestName());
            assertEquals("First", e.getSecondTestName());
            assertEquals(0.033, e.getSecondMeasure().mean(), 1E-3);
            assertEquals(0.066, e.getFirstMeasure().mean(), 1E-3);
            assertEquals(1.0, e.getTolerance(), 1E-3);
            return;
        }
        fail();
    }

    @Test
    public void shouldNotBeSlower() {
        final PerformanceAssertion ap = AssertPerformance.withTolerance(1F)
            .assertTest("First").slowerThan("Second");

        final PerformanceStats lp = FakePerformanceCreator.createStats(1_000,
                new Object[][] {
                    {"First", 33}, {"Second", 66}, {"Top", 100}
                });

        try {
            ap.check(lp);
        } catch (OrderAssertionError e) {
            assertEquals(OrderCondition.SLOWER, e.getRequiredCondition());
            assertEquals("First", e.getFirstTestName());
            assertEquals("Second", e.getSecondTestName());
            assertEquals(0.033, e.getFirstMeasure().mean(), 1E-3);
            assertEquals(0.066, e.getSecondMeasure().mean(), 1E-3);
            assertEquals(1.0, e.getTolerance(), 1E-3);
            return;
        }
        fail();
    }

    @Test
    public void shouldNotBeEquals() {
        final PerformanceAssertion ap = AssertPerformance.withTolerance(1F)
            .assertTest("First").sameAs("Second");

        final PerformanceStats stats = FakePerformanceCreator.createStats(1_000,
                new Object[][] {
                    {"First", 33}, {"Second", 66}, {"Top", 100}
                });

        try {
            ap.check(stats);
            fail();
        } catch (OrderAssertionError e) {
            assertEquals(OrderCondition.SAME, e.getRequiredCondition());
            assertEquals("Second", e.getSecondTestName());
            assertEquals("First", e.getFirstTestName());
            assertEquals(0.066, e.getSecondMeasure().mean(), 1E-3);
            assertEquals(0.033, e.getFirstMeasure().mean(), 1E-3);
        }
    }

    @Test
    public void shouldReportNonExistentTest() {
        final PerformanceAssertion ap = AssertPerformance.withTolerance(1F)
            .assertTest("First").sameAs("NonExistent");

        final PerformanceStats stats = FakePerformanceCreator.createStats(1_000,
                new Object[][] {
                    {"First", 33}, {"Second", 66}, {"Top", 100}
                });

        try {
            ap.check(stats);
            fail();
        } catch (IllegalStateException e) {
            assertEquals("Test 'NonExistent' not found, " +
                    "valid tests are: [First, Second, Top]",
                    e.getMessage());
        }
    }

    @Test
    public void shouldCheckTwoTestsSimultaneously() {
        final PerformanceAssertion ap = AssertPerformance.withTolerance(1F)
            .assertTest("First").fasterThan("Second")
            .assertTest("Second").fasterThan("Top");

        final PerformanceStats stats = FakePerformanceCreator.createStats(1_000,
                new Object[][] {
                    {"First", 33}, {"Second", 66}, {"Top", 100}
                });

        try {
            ap.check(stats);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    //(5.34653740395317 ± 0.03210949319450669 (74 samples, 33603400 iterations) ns)
    //expected same as reference' (5.0513496559962086 ± 0.025385146660952432
    //(66 samples, 29970600 iterations) ns)  with a tolerance of 1.0
    private static class MeasureImpl extends FakeMeasure {
        MeasureImpl(double mean, double standardError) {
            this.mean = mean;
            this.standardError = standardError;
        }

        @Override
        public double marginOfError(double confidence) {
            return standardError() * StatFunctions.zeta(confidence);
        }

        @Override
        public MarginOfErrorConfidenceInterval getConfidenceInterval(
                double confidence) {
            return new MarginOfErrorConfidenceInterval(mean,
                    marginOfError(confidence), confidence);
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
