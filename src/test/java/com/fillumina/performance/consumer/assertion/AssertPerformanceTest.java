package com.fillumina.performance.consumer.assertion;

import com.fillumina.performance.producer.timer.LoopPerformances;
import com.fillumina.performance.producer.timer.LoopPerformancesCreator;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author fra
 */
public class AssertPerformanceTest {

    @Test
    public void shouldConfirmTheExpectedPercentages() {
        final AssertPerformance ap = AssertPerformance.withTolerance(1F);

        ap.assertPercentageFor("First").equals(33F);
        ap.assertPercentageFor("Second").equals(66F);

        final LoopPerformances lp = LoopPerformancesCreator.parse(1_000,
                new Object[][] {
                    {"First", 33}, {"Second", 66}, {"Top", 100}
                });

        ap.check(lp);
    }

    @Test
    public void shouldRiseAnAssertionErrorForUnexpectedPercentage() {
        final AssertPerformance ap = AssertPerformance.withTolerance(1F);

        ap.assertPercentageFor("First").equals(10F);

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
                    "found 33.00 % with tolerance of 1.0 %",
                    e.getMessage());
        }
    }

    @Test
    public void shouldConfirmTheExpectedOrder() {
        final AssertPerformance ap = AssertPerformance.withTolerance(1F);

        ap.assertTest("First").fasterThan("Second");

        final LoopPerformances lp = LoopPerformancesCreator.parse(1_000,
                new Object[][] {
                    {"First", 33}, {"Second", 66}, {"Top", 100}
                });

        ap.check(lp);
    }

    @Test
    public void shouldRiseAnAssertionErrorForAnUnexpectedOrder() {
        final AssertPerformance ap = AssertPerformance.withTolerance(1F);

        ap.assertTest("First").slowerThan("Second");

        final LoopPerformances lp = LoopPerformancesCreator.parse(1_000,
                new Object[][] {
                    {"First", 33}, {"Second", 66}, {"Top", 100}
                });

        try {
            ap.check(lp);
            fail();
        } catch (AssertionError e) {
            assertEquals(" 'First' (33.00 %) was faster than 'Second' (66.00 %) " +
                    "with tolerance of 1.0 %",
                    e.getMessage());
        }
    }

    @Test
    public void shouldRiseAnAssertionErrorForAnUnmatchedOrder() {
        final AssertPerformance ap = AssertPerformance.withTolerance(1F);

        ap.assertTest("First").equalsTo("Second");

        final LoopPerformances lp = LoopPerformancesCreator.parse(1_000,
                new Object[][] {
                    {"First", 33}, {"Second", 66}, {"Top", 100}
                });

        try {
            ap.check(lp);
            fail();
        } catch (AssertionError e) {
            assertEquals(" 'First' (33.00 %) was not equals to 'Second' (66.00 %) " +
                    "with tolerance of 1.0 %",
                    e.getMessage());
        }
    }

}
