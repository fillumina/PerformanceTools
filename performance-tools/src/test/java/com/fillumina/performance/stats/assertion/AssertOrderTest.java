package com.fillumina.performance.stats.assertion;

import com.fillumina.performance.stats.FakePerformanceCreator;
import com.fillumina.performance.stats.PerformanceStats;
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
    public void shouldRiseAnAssertionErrorIfUnexpetectlySlower() {
        final PerformanceAssertion ap = AssertPerformance.withTolerance(1F)
            .assertTest("First").slowerThan("Second");

        final PerformanceStats stats = FakePerformanceCreator.createStats(1_000,
                new Object[][] {
                    {"First", 33}, {"Second", 66}, {"Top", 100}
                });

        try {
            ap.check(stats);
        } catch (OrderAssertionError e) {
            assertEquals(OrderCondition.SLOWER, e.getRequiredCondition());
            assertEquals("Second", e.getSecondTestName());
            assertEquals("First", e.getFirstTestName());
            assertEquals(0.066, e.getSecondMeasure().mean(), 1E-3);
            assertEquals(0.033, e.getFirstMeasure().mean(), 1E-3);
            assertEquals(1.0, e.getTolerance(), 1E-3);
            return;
//            assertEquals(" 'First' (0.033 ± 0.0 (10 samples)) was faster than " +
//                    "'Second' (0.066 ± 0.0 (10 samples)) with a tolerance of 1.0 %",
//                    e.getMessage());
        }
        fail();
    }

    @Test
    public void shouldRiseAnAssertionErrorIfUnexpectedlyFaster() {
        final PerformanceAssertion ap = AssertPerformance.withTolerance(1F)
            .assertTest("Second").fasterThan("First");

        final PerformanceStats lp = FakePerformanceCreator.createStats(1_000,
                new Object[][] {
                    {"First", 33}, {"Second", 66}, {"Top", 100}
                });

        try {
            ap.check(lp);
        } catch (OrderAssertionError e) {
            assertEquals(OrderCondition.FASTER, e.getRequiredCondition());
            assertEquals("Second", e.getFirstTestName());
            assertEquals("First", e.getSecondTestName());
            assertEquals(0.066, e.getFirstMeasure().mean(), 1E-3);
            assertEquals(0.033, e.getSecondMeasure().mean(), 1E-3);
            assertEquals(1.0, e.getTolerance(), 1E-3);
            return;
//            assertEquals(" 'Second' (0.066 ± 0.0 (10 samples)) " +
//                    "was slower than 'First' (0.033 ± 0.0 (10 samples)) " +
//                    "with a tolerance of 1.0 %",
//                    e.getMessage());
        }
        fail();
    }

    @Test
    public void shouldRiseAnAssertionErrorIfUnmatchedOrder() {
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
            assertEquals(1.0, e.getTolerance(), 1E-3);
//            assertEquals(" 'First' (0.033 ± 0.0 (10 samples)) was not equals to " +
//                    "'Second' (0.066 ± 0.0 (10 samples)) with a tolerance of 1.0 %",
//                    e.getMessage());
        }
    }

    @Test
    public void shouldRiseAnExceptionIfRequestingANonExistentTest() {
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
    public void shouldMakeTwoTestSimultaneously() {
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

    //(5.34653740395317 ± 0.03210949319450669 (74 samples, 33603400 iterations) ns)
    //expected same as reference' (5.0513496559962086 ± 0.025385146660952432
    //(66 samples, 29970600 iterations) ns)  with a tolerance of 1.0

    @Test
    public void shouldBeEqualConsideringTolerance() {
//        AssertOrder.AssertOrderChecker aoc = new AssertOrder.AssertOrderChecker();
    }
}
