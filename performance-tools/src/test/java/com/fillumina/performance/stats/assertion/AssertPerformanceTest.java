package com.fillumina.performance.stats.assertion;

import com.fillumina.performance.stats.FakePerformanceCreator;
import com.fillumina.performance.stats.PerformanceStats;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati
 */
public class AssertPerformanceTest {

    @Test
    public void shouldConfirmTheExpectedPercentages() {
        final PerformanceAssertion ap = AssertPerformance.withTolerance(1F)
            .assertPercentageFor("First").sameAs(33F)
            .assertPercentageFor("Second").sameAs(66F);

        final PerformanceStats stats = FakePerformanceCreator.createStats(1_000,
                new Object[][] {
                    {"First", 33}, {"Second", 66}, {"Top", 100}
                });

        ap.check(stats);
    }

    @Test
    public void shouldRiseAnAssertionErrorIfUnexpectedlyGreater() {
        final PerformanceAssertion ap = AssertPerformance.withTolerance(1F)
            .assertPercentageFor("First").greaterThan(50F);

        final PerformanceStats lp = FakePerformanceCreator.createStats(1_000,
                new Object[][] {
                    {"First", 33}, {"Second", 66}, {"Top", 100}
                });

        try {
            ap.check(lp);
            fail();
        } catch (AssertionError e) {
            assertEquals(
                    " 'First' expected greater than 50.00 %, " +
                    "found 33.00000 ± 0.00000 % (confidence 99.9000 %) " +
                    "with a tolerance of 1.0 %",
                    e.getMessage());
        }
    }

    @Test
    public void shouldRiseAnAssertionErrorIfUnexpectedlyLesser() {
        final PerformanceAssertion ap = AssertPerformance.withTolerance(1F)
            .assertPercentageFor("First").lessThan(10F);

        final PerformanceStats lp = FakePerformanceCreator.createStats(1_000,
                new Object[][] {
                    {"First", 33}, {"Second", 66}, {"Top", 100}
                });

        try {
            ap.check(lp);
            fail();
        } catch (AssertionError e) {
            assertEquals(
                    " 'First' expected lesser than 10.00 %, " +
                    "found 33.00000 ± 0.00000 % (confidence 99.9000 %) " +
                    "with a tolerance of 1.0 %",
                    e.getMessage());
        }
    }

    @Test
    public void shouldRiseAnAssertionErrorIfUnexpectedlyEquals() {
        final PerformanceAssertion ap = AssertPerformance.withTolerance(1F)
            .assertPercentageFor("First").sameAs(10F);

        final PerformanceStats lp = FakePerformanceCreator.createStats(1_000,
                new Object[][] {
                    {"First", 33}, {"Second", 66}, {"Top", 100}
                });

        try {
            ap.check(lp);
            fail();
        } catch (AssertionError e) {
            assertEquals(
                    " 'First' expected equals to 10.00 %, " +
                    "found 33.00000 ± 0.00000 % (confidence 99.9000 %) " +
                    "with a tolerance of 1.0 %",
                    e.getMessage());
        }
    }

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

        final PerformanceStats lp = FakePerformanceCreator.createStats(1_000,
                new Object[][] {
                    {"First", 33}, {"Second", 66}, {"Top", 100}
                });

        try {
            ap.check(lp);
            fail();
        } catch (AssertionError e) {
            assertEquals(" 'First' (0.033 ± 0.0 (10 samples)) was faster than " +
                    "'Second' (0.066 ± 0.0 (10 samples)) with a tolerance of 1.0 %",
                    e.getMessage());
        }
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
            fail();
        } catch (AssertionError e) {
            assertEquals(" 'Second' (0.066 ± 0.0 (10 samples)) " +
                    "was slower than 'First' (0.033 ± 0.0 (10 samples)) " +
                    "with a tolerance of 1.0 %",
                    e.getMessage());
        }
    }

    @Test
    public void shouldRiseAnAssertionErrorIfUnmatchedOrder() {
        final PerformanceAssertion ap = AssertPerformance.withTolerance(1F)
            .assertTest("First").sameAs("Second");

        final PerformanceStats lp = FakePerformanceCreator.createStats(1_000,
                new Object[][] {
                    {"First", 33}, {"Second", 66}, {"Top", 100}
                });

        try {
            ap.check(lp);
            fail();
        } catch (AssertionError e) {
            assertEquals(" 'First' (0.033 ± 0.0 (10 samples)) was not equals to " +
                    "'Second' (0.066 ± 0.0 (10 samples)) with a tolerance of 1.0 %",
                    e.getMessage());
        }
    }

    @Test
    public void shouldRiseAnExceptionIfRequestingANonExistentTest() {
        final PerformanceAssertion ap = AssertPerformance.withTolerance(1F)
            .assertTest("First").sameAs("NonExistent");

        final PerformanceStats lp = FakePerformanceCreator.createStats(1_000,
                new Object[][] {
                    {"First", 33}, {"Second", 66}, {"Top", 100}
                });

        try {
            ap.check(lp);
            fail();
        } catch (IllegalStateException e) {
            assertEquals("Test 'NonExistent' not found",
                    e.getMessage());
        }
    }
}
