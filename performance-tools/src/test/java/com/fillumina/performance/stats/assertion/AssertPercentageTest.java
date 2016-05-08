package com.fillumina.performance.stats.assertion;

import com.fillumina.performance.stats.FakePerformanceCreator;
import com.fillumina.performance.stats.PerformanceStats;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati
 */
public class AssertPercentageTest {

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

        final PerformanceStats stats = FakePerformanceCreator.createStats(1_000,
                new Object[][] {
                    {"First", 33}, {"Second", 66}, {"Top", 100}
                });

        try {
            ap.check(stats);
        } catch (PercentageAssertionError e) {
            assertEquals("First", e.getTestName());
            assertEquals(0.33, e.getRatio().getValue(), 1E-3);
            assertEquals(1.0, e.getTolerance(), 0);
            return;
//            System.out.println(e);
//            assertEquals(
//                    " 'First' expected greater than 50.00 %, " +
//                    "found 33.00000 ± 0.00000 % (confidence 99.9000 %) " +
//                    "with a tolerance of 1.0 %",
//                    e.getMessage());
        }
        fail();
    }

    @Test
    public void shouldRiseAnAssertionErrorIfUnexpectedlyLesser() {
        final PerformanceAssertion ap = AssertPerformance.withTolerance(1F)
            .assertPercentageFor("First").lessThan(10F);

        final PerformanceStats stats = FakePerformanceCreator.createStats(1_000,
                new Object[][] {
                    {"First", 33}, {"Second", 66}, {"Top", 100}
                });

        try {
            ap.check(stats);
        } catch (PercentageAssertionError e) {
            assertEquals("First", e.getTestName());
            assertEquals(0.33, e.getRatio().getValue(), 1E-3);
            assertEquals(1.0, e.getTolerance(), 0);
            return;
//            assertEquals(
//                    " 'First' expected lesser than 10.00 %, " +
//                    "found 33.00000 ± 0.00000 % (confidence 99.9000 %) " +
//                    "with a tolerance of 1.0 %",
//                    e.getMessage());
        }
        fail();
    }

    @Test
    public void shouldRiseAnAssertionErrorIfUnexpectedlyEquals() {
        final PerformanceAssertion ap = AssertPerformance.withTolerance(1F)
            .assertPercentageFor("First").sameAs(10F);

        final PerformanceStats stats = FakePerformanceCreator.createStats(1_000,
                new Object[][] {
                    {"First", 33}, {"Second", 66}, {"Top", 100}
                });

        try {
            ap.check(stats);
        } catch (PercentageAssertionError e) {
            assertEquals("First", e.getTestName());
            assertEquals(0.33, e.getRatio().getValue(), 1E-3);
            assertEquals(1.0, e.getTolerance(), 0);
            return;
//            assertEquals("'First' expected equals to 10.00 %, " +
//                    "found 33.00000 ± 0.00000 % (confidence 95.0000 %) " +
//                    "with a tolerance of 1.0 %",
//                    e.getMessage());
        }
        fail();
    }
}
