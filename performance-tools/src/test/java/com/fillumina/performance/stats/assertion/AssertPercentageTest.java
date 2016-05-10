package com.fillumina.performance.stats.assertion;

import com.fillumina.performance.stats.FakePerformanceCreator;
import com.fillumina.performance.stats.PerformanceStats;
import com.fillumina.performance.stats.assertion.AssertPercentage.AssertPercentageCondition;
import com.fillumina.performance.util.stats.MeasureRatio;
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
            .assertPercentage("First").sameAs(33F)
            .assertPercentage("Second").sameAs(66F);

        final PerformanceStats stats = FakePerformanceCreator.createStats(1_000,
                new Object[][] {
                    {"First", 33}, {"Second", 66}, {"Top", 100}
                });

        ap.check(stats);
    }

    @Test
    public void shouldNotBeGreater() {
        final PerformanceAssertion ap = AssertPerformance.withTolerance(1F)
            .assertPercentage("First").greaterThan(50F);

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
        }
        fail();
    }

    @Test
    public void shouldNotBeLesser() {
        final PerformanceAssertion ap = AssertPerformance.withTolerance(1F)
            .assertPercentage("First").lessThan(10F);

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
        }
        fail();
    }

    @Test
    public void shouldNotBeEquals() {
        final PerformanceAssertion ap = AssertPerformance.withTolerance(1F)
            .assertPercentage("First").sameAs(10F);

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
        }
        fail();
    }

    // 'zero' expected equals to 0.00 %,
    // found 1.39349 ± 0.23454 % (confidence 95.0000 %)
    // with a tolerance of 1.0 %
    @Test
    public void shouldBeEqualsConsideringTolerance() {
        MeasureRatio perc = new MeasureRatio(
                1.39349, 0.0075, 30,
                100.00,  4.123, 30,
                1.0);
        assertEquals(1.39730, perc.getValue() * 100, 1E-3);
        assertEquals(0.23460, perc.getMarginOfError() * 100, 1E-3);
        final boolean comply = AssertPercentageCondition.comply(perc,
                        0f,
                        2.0, // percentage points
                        PercentageCondition.EQUALS);
        assertTrue(comply);
    }
}
