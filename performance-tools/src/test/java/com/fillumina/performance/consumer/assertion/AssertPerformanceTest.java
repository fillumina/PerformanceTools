package com.fillumina.performance.consumer.assertion;

import com.fillumina.performance.producer.LoopPerformances;
import com.fillumina.performance.producer.timer.LoopPerformancesCreator;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AssertPerformanceTest {

    @Test
    public void shouldConfirmTheExpectedPercentages() {
        final AssertPerformance ap = AssertPerformance.withTolerance(1F)
            .assertPercentageFor("First").sameAs(33F)
            .assertPercentageFor("Second").sameAs(66F);

        final LoopPerformances lp = LoopPerformancesCreator.parse(1_000,
                new Object[][] {
                    {"First", 33}, {"Second", 66}, {"Top", 100}
                });

        ap.check(lp);
    }

    @Test
    public void shouldRiseAnAssertionErrorIfUnexpectedlyGreater() {
        final AssertPerformance ap = AssertPerformance.withTolerance(1F)
            .assertPercentageFor("First").greaterThan(50F);

        final LoopPerformances lp = LoopPerformancesCreator.parse(1_000,
                new Object[][] {
                    {"First", 33}, {"Second", 66}, {"Top", 100}
                });

        try {
            ap.check(lp);
            fail();
        } catch (AssertionError e) {
            assertEquals(
                    " 'First' expected greater than 50.00 %, " +
                    "found 33.00 % with a tolerance of 1.0 %",
                    e.getMessage());
        }
    }

    @Test
    public void shouldRiseAnAssertionErrorIfUnexpectedlyLesser() {
        final AssertPerformance ap = AssertPerformance.withTolerance(1F)
            .assertPercentageFor("First").lessThan(10F);

        final LoopPerformances lp = LoopPerformancesCreator.parse(1_000,
                new Object[][] {
                    {"First", 33}, {"Second", 66}, {"Top", 100}
                });

        try {
            ap.check(lp);
            fail();
        } catch (AssertionError e) {
            assertEquals(
                    " 'First' expected lesser than 10.00 %, " +
                    "found 33.00 % with a tolerance of 1.0 %",
                    e.getMessage());
        }
    }

    @Test
    public void shouldRiseAnAssertionErrorIfUnexpectedlyEquals() {
        final AssertPerformance ap = AssertPerformance.withTolerance(1F)
            .assertPercentageFor("First").sameAs(10F);

        final LoopPerformances lp = LoopPerformancesCreator.parse(1_000,
                new Object[][] {
                    {"First", 33}, {"Second", 66}, {"Top", 100}
                });

        try {
            ap.check(lp);
            fail();
        } catch (AssertionError e) {
            assertEquals(
                    " 'First' expected equals to 10.00 %, " +
                    "found 33.00 % with a tolerance of 1.0 %",
                    e.getMessage());
        }
    }

    @Test
    public void shouldConfirmTheExpectedOrder() {
        final AssertPerformance ap = AssertPerformance.withTolerance(1F)
            .assertTest("First").fasterThan("Second");

        final LoopPerformances lp = LoopPerformancesCreator.parse(1_000,
                new Object[][] {
                    {"First", 33}, {"Second", 66}, {"Top", 100}
                });

        ap.check(lp);
    }

    @Test
    public void shouldRiseAnAssertionErrorIfUnexpetectlySlower() {
        final AssertPerformance ap = AssertPerformance.withTolerance(1F)
            .assertTest("First").slowerThan("Second");

        final LoopPerformances lp = LoopPerformancesCreator.parse(1_000,
                new Object[][] {
                    {"First", 33}, {"Second", 66}, {"Top", 100}
                });

        try {
            ap.check(lp);
            fail();
        } catch (AssertionError e) {
            assertEquals(" 'First' (33.00 %) was faster than 'Second' (66.00 %) " +
                    "with a tolerance of 1.0 %",
                    e.getMessage());
        }
    }

    @Test
    public void shouldRiseAnAssertionErrorIfUnexpectedlyFaster() {
        final AssertPerformance ap = AssertPerformance.withTolerance(1F)
            .assertTest("Second").fasterThan("First");

        final LoopPerformances lp = LoopPerformancesCreator.parse(1_000,
                new Object[][] {
                    {"First", 33}, {"Second", 66}, {"Top", 100}
                });

        try {
            ap.check(lp);
            fail();
        } catch (AssertionError e) {
            assertEquals(" 'Second' (66.00 %) was slower than 'First' (33.00 %) " +
                    "with a tolerance of 1.0 %",
                    e.getMessage());
        }
    }

    @Test
    public void shouldRiseAnAssertionErrorIfUnmatchedOrder() {
        final AssertPerformance ap = AssertPerformance.withTolerance(1F)
            .assertTest("First").sameAs("Second");

        final LoopPerformances lp = LoopPerformancesCreator.parse(1_000,
                new Object[][] {
                    {"First", 33}, {"Second", 66}, {"Top", 100}
                });

        try {
            ap.check(lp);
            fail();
        } catch (AssertionError e) {
            assertEquals(" 'First' (33.00 %) was not equals to 'Second' (66.00 %) " +
                    "with a tolerance of 1.0 %",
                    e.getMessage());
        }
    }

    @Test
    public void shouldRiseAnExceptionIfRequestingANonExistentTest() {
        final AssertPerformance ap = AssertPerformance.withTolerance(1F)
            .assertTest("First").sameAs("NonExistent");

        final LoopPerformances lp = LoopPerformancesCreator.parse(1_000,
                new Object[][] {
                    {"First", 33}, {"Second", 66}, {"Top", 100}
                });

        try {
            ap.check(lp);
            fail();
        } catch (IllegalStateException e) {
            assertEquals("Test 'NonExistent' does not exist!",
                    e.getMessage());
        }
    }
}
