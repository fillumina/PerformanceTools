package com.fillumina.performance.assertion;

import com.fillumina.performance.mock.AssertableMock;
import com.fillumina.performance.util.stats.Ratio;
import com.fillumina.performance.util.unit.Absolute;
import com.fillumina.performance.util.unit.IntervalUnit;
import com.fillumina.performance.util.unit.Magnitude;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class QuantityEvaluatorTest {

    @Test(expected = ExperimentAssertionError.class)
    public void shouldCheckAndThrowException() {
        ExperimentAssertion assertion =
                Assertions.withTolerance(Ratio.percentage(3))
                        .assertQuantity("first")
                        .equalsTo(Absolute.UNIT.quantity(23));

        AssertableMock assertable = AssertableMock.create(
                "first", 12.3, "second", 45.6, "third", 34.5);

        assertion.check(assertable);
        fail("shouln't be here");
    }

    @Test
    public void shouldConsumeLessThanAndBeOk() {
        ExperimentAssertion assertion =
                Assertions.withTolerance(Ratio.percentage(5))
                        .assertQuantity("first")
                        .equalsTo(Absolute.UNIT.quantity(11.8));

        AssertableMock assertable = AssertableMock.create(
                "first", 12.3, "second", 45.6, "third", 34.5);

        assertion.check(assertable);
    }

    @Test(expected = ExperimentAssertionError.class)
    public void shouldCheckEqualsAndThrowException() {
        ExperimentAssertion assertion =
                Assertions.withTolerance(Ratio.percentage(3))
                        .assertQuantity("first")
                        .equalsTo(Absolute.UNIT.quantity(23));

        AssertableMock assertable = AssertableMock.create(
                "first", 12.3, "second", 45.6, "third", 34.5);

        assertion.check(assertable);
    }

    @Test
    public void shouldBeEqualsWithDifferentUnits() {
        ExperimentAssertion assertion =
                Assertions.withTolerance(Ratio.percentage(3))
                        .assertQuantity("first")
                        .equalsTo(Magnitude.MILLI.quantity(12.3E3));

        AssertableMock assertable = AssertableMock.createWithNameAndUnit(
                "", Magnitude.UNIT,
                "first", 12.3, "second", 45.6, "third", 34.5);

        assertion.check(assertable);
    }

    // TODO RuntimeException?
    @Test(expected = RuntimeException.class)
    public void shouldNotAcceptsDifferentDimensions() {
        ExperimentAssertion assertion =
                Assertions.withTolerance(Ratio.percentage(3))
                        .assertQuantity("first")
                        // this unit is not the same as magnitude used in
                        // AssertableMock
                        .equalsTo(IntervalUnit.HOURS.quantity(12.3));

        AssertableMock assertable = AssertableMock.create(
                "first", 12.3, "second", 45.6, "third", 34.5);

        assertion.check(assertable);
    }

    @Test
    public void shouldConsumeNotEquals() {
        ExperimentAssertion assertion =
                Assertions.withTolerance(Ratio.ZERO)
                        .assertQuantity("first")
                        .notEqualsTo(Absolute.UNIT.quantity(12.3));

        AssertableMock assertable = AssertableMock.create(
                "first", 45, "second", 45.6, "third", 34.5);

        assertTrue(assertion.satisfy(assertable));
    }

    /**
     * Note that not equals works only with 0 tolerance.
     */
    @Test(expected = ExperimentAssertionError.class)
    public void shouldThrowNotEquals() {
        ExperimentAssertion assertion =
                Assertions.withTolerance(Ratio.ZERO)
                        .assertQuantity("first")
                        .notEqualsTo(Absolute.UNIT.quantity(12.3));

        AssertableMock assertable = AssertableMock.create(
                "first", 12.3, "second", 45.6, "third", 34.5);

        assertion.check(assertable);
    }

    public void shouldNotBeNotEqualsIfToleranceDifferentFromZero() {
        ExperimentAssertion assertion =
                Assertions.withTolerance(Ratio.percentage(3))
                        .assertQuantity("first")
                        .notEqualsTo(Absolute.UNIT.quantity(12.3));

        AssertableMock assertable = AssertableMock.create(
                "first", 12.3, "second", 45.6, "third", 34.5);

        assertion.check(assertable);
    }

    @Test
    public void shouldConsumeLessThanOrEquals() {
        ExperimentAssertion assertion =
                Assertions.withTolerance(Ratio.percentage(5))
                        .assertQuantity("first")
                        .lessThanOrEquals(Absolute.UNIT.quantity(12.3));

        AssertableMock assertable = AssertableMock.create(
                "first", 12.3, "second", 45.6, "third", 34.5);

        assertTrue(assertion.satisfy(assertable));
    }

    @Test
    public void shouldConsumeGreaterThanOrEquals() {
        ExperimentAssertion assertion =
                Assertions.withTolerance(Ratio.percentage(5))
                        .assertQuantity("first")
                        .greaterThanOrEquals(Absolute.UNIT.quantity(5.2));

        AssertableMock assertable = AssertableMock.create(
                "first", 12.3, "second", 45.6, "third", 34.5);

        assertTrue(assertion.satisfy(assertable));
    }

    @Test
    public void shouldNotConsumeGreaterThanOrEquals() {
        ExperimentAssertion assertion =
                Assertions.withTolerance(Ratio.percentage(5))
                        .assertQuantity("first")
                        .greaterThanOrEquals(Absolute.UNIT.quantity(5.2));

        AssertableMock assertable = AssertableMock.create(
                "first", 12.3, "second", 45.6, "third", 34.5);

        assertTrue(assertion.satisfy(assertable));
    }

    public static void main(final String[] args) {
        ExperimentAssertion assertion =
                Assertions.withTolerance(Ratio.percentage(3))
                        .assertQuantity("first")
                        .lessThanOrEquals(Absolute.UNIT.quantity(23));

        AssertableMock assertable = AssertableMock.create(
                "first", 12.3, "second", 45.6, "third", 34.5);

        try {
            assertion.check(assertable);
            System.out.println("Assertion Accepted: " + assertion);
        } catch(ExperimentAssertionError e) {
            System.out.println(e);
        }
    }
}
