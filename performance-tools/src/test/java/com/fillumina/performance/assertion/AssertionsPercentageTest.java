package com.fillumina.performance.assertion;

import com.fillumina.performance.mock.AssertableMock;
import com.fillumina.performance.util.stats.Ratio;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati
 */
public class AssertionsPercentageTest {

    @Test
    public void shouldConfirmTheExpectedPercentages() {
        final Assertions assertion =
                Assertions.withTolerance(Ratio.percentage(1))
            .assertRatioPercentage("First").equalsTo(33)
            .assertRatioPercentage("Second").equalsTo(66);

        final AssertableMock assertable =
                AssertableMock.create(
                        "First", 33,
                        "Second", 66,
                        "Top", 100);

        assertion.check(assertable);
    }

    @Test
    public void shouldNotBeGreater() {
        final Assertions assertion =
                Assertions.withTolerance(Ratio.percentage(1))
            .assertRatioPercentage("First").greaterThan(50);

        final AssertableMock assertable =
                AssertableMock.create(
                        "First", 33,
                        "Second", 66,
                        "Top", 100);

        try {
            assertion.check(assertable);
        } catch (ExperimentAssertionError ex) {
            RatioValueInfo e = (RatioValueInfo) ex.getInfo();

            assertEquals("First", e.getFirstTestName().toString());
            assertEquals(50.0, e.getAssertionValue().doubleValue(), 1E-3);
            assertEquals(1.0, e.getTolerance().getPercentage(), 0);
            return;
        }
        fail();
    }

    @Test
    public void shouldNotBeLesser() {
        final Assertions assertion =
                Assertions.withTolerance(Ratio.percentage(1))
            .assertRatioPercentage("First").lessThan(10F);

        final AssertableMock assertable =
                AssertableMock.create(
                        "First", 33,
                        "Second", 66,
                        "Top", 100);

        try {
            assertion.check(assertable);
        } catch (ExperimentAssertionError ex) {
            RatioValueInfo e = (RatioValueInfo) ex.getInfo();

            assertEquals("First", e.getFirstTestName().toString());
            assertEquals(10.0, e.getAssertionValue().doubleValue(), 1E-3);
            assertEquals(1.0, e.getTolerance().getPercentage(), 0);
            return;
        }
        fail();
    }

    @Test
    public void shouldNotBeEquals() {
        final Assertions assertion =
                Assertions.withTolerance(Ratio.percentage(1))
            .assertRatioPercentage("First").equalsTo(10F);

        final AssertableMock assertable =
                AssertableMock.create(
                        "First", 33,
                        "Second", 66,
                        "Top", 100);

        try {
            assertion.check(assertable);
        } catch (ExperimentAssertionError ex) {
            RatioValueInfo e = (RatioValueInfo) ex.getInfo();

            assertEquals("First", e.getFirstTestName().toString());
            assertEquals(10.0, e.getAssertionValue().doubleValue(), 1E-3);
            assertEquals(1.0, e.getTolerance().getPercentage(), 0);
            return;
        }
        fail();
    }
}
