package com.fillumina.performance.assertion;

import com.fillumina.performance.mock.AssertableMock;
import com.fillumina.performance.util.RelativeOrder;
import com.fillumina.performance.util.stats.Ratio;
import com.fillumina.performance.util.unit.Absolute;
import com.fillumina.performance.util.unit.IntervalUnit;
import com.fillumina.performance.util.unit.Magnitude;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ValueAssertionTest {

    @Test(expected = ValueAssertionError.class)
    public void shouldConsumeAndThrowException() {
        ValueAssertion assertion =
                new ValueAssertion(
                        "first",
                        RelativeOrder.EQUALS,
                        Absolute.UNIT.quantity(23),
                        Ratio.percentage(3));

        AssertableMock assertable = AssertableMock.create(
                "first", 12.3, "second", 45.6, "third", 34.5);

        assertion.check(assertable);
        throw new RuntimeException("shouln't be here");
    }

    @Test
    public void shouldConsumeLessThanAndBeOk() {
        ValueAssertion assertion =
                new ValueAssertion(
                        "first",
                        RelativeOrder.EQUALS,
                        Absolute.UNIT.quantity(11.8),
                        Ratio.percentage(5));

        AssertableMock assertable = AssertableMock.create(
                "first", 12.3, "second", 45.6, "third", 34.5);

        assertion.check(assertable);
    }

    @Test(expected = ValueAssertionError.class)
    public void shouldConsumeEqualsAndThrowException() {
        ValueAssertion assertion =
                new ValueAssertion(
                        "first",
                        RelativeOrder.EQUALS,
                        Absolute.UNIT.quantity(23),
                        Ratio.percentage(3));

        AssertableMock assertable = AssertableMock.create(
                "first", 12.3, "second", 45.6, "third", 34.5);

        assertion.check(assertable);
    }

    @Test
    public void shouldBeEqualsWithDifferentUnits() {
        ValueAssertion assertion =
                new ValueAssertion(
                        "first",
                        RelativeOrder.EQUALS,
                        Magnitude.MILLI.quantity(12.3E3),
                        Ratio.percentage(3));

        AssertableMock assertable = AssertableMock.createWithNameAndUnit("",
                Magnitude.UNIT,
                "first", 12.3, "second", 45.6, "third", 34.5);

        assertion.check(assertable);
    }

    @Test(expected = RuntimeException.class)
    public void shouldNotAcceptsDifferentDimensions() {
        ValueAssertion assertion =
                new ValueAssertion(
                        "first",
                        RelativeOrder.EQUALS,
                        // this unit is not the same as magnitude used in
                        // AssertableMock
                        IntervalUnit.HOURS.quantity(12.3),
                        Ratio.percentage(3));

        AssertableMock assertable = AssertableMock.create(
                "first", 12.3, "second", 45.6, "third", 34.5);

        assertion.check(assertable);
    }

    @Test
    public void shouldConsumeNotEquals() {
        ValueAssertion assertion =
                new ValueAssertion(
                        "first",
                        RelativeOrder.EQUALS,
                        Absolute.UNIT.quantity(12.3),
                        Ratio.percentage(5));

        AssertableMock assertable = AssertableMock.create(
                "first", 12.3, "second", 45.6, "third", 34.5);

        assertFalse(new NegateExperimentAssertion(assertion).satisfy(assertable));
    }

    @Test(expected = ExperimentAssertionError.class)
    public void shouldThrowNotEquals() {
        ValueAssertion assertion =
                new ValueAssertion(
                        "first",
                        RelativeOrder.EQUALS,
                        Absolute.UNIT.quantity(12.3),
                        Ratio.percentage(5));

        AssertableMock assertable = AssertableMock.create(
                "first", 12.3, "second", 45.6, "third", 34.5);

        new NegateExperimentAssertion(assertion).check(assertable);
    }

    @Test
    public void shouldConsumeLessThanOrEquals() {
        ValueAssertion assertion =
                new ValueAssertion(
                        "first",
                        RelativeOrder.GREATER,
                        Absolute.UNIT.quantity(20.0),
                        Ratio.percentage(5));

        AssertableMock assertable = AssertableMock.create(
                "first", 12.3, "second", 45.6, "third", 34.5);

        assertTrue(new NegateExperimentAssertion(assertion).satisfy(assertable));
    }

    @Test
    public void shouldConsumeGreaterThanOrEquals() {
        ValueAssertion assertion =
                new ValueAssertion(
                        "first",
                        RelativeOrder.LESS,
                        Absolute.UNIT.quantity(5.2),
                        Ratio.percentage(5));

        AssertableMock assertable = AssertableMock.create(
                "first", 12.3, "second", 45.6, "third", 34.5);

        assertTrue(new NegateExperimentAssertion(assertion).satisfy(assertable));
    }

    @Test
    public void shouldNotConsumeGreaterThanOrEquals() {
        ValueAssertion assertion =
                new ValueAssertion(
                        "first",
                        RelativeOrder.LESS,
                        Absolute.UNIT.quantity(5.2),
                        Ratio.percentage(5));

        AssertableMock assertable = AssertableMock.create(
                "first", 12.3, "second", 45.6, "third", 34.5);

        assertTrue(new NegateExperimentAssertion(assertion).satisfy(assertable));
    }

    public static void main(final String[] args) {
        ValueAssertion assertion =
                new ValueAssertion(
                        "first",
                        RelativeOrder.EQUALS,
                        Magnitude.UNIT.quantity(23),
                        Ratio.percentage(3));

        AssertableMock assertable = AssertableMock.create(
                "first", 12.3, "second", 45.6, "third", 34.5);

        try {
            assertion.check(assertable);
        } catch(ValueAssertionError e) {
            System.out.println(e);
        }
    }
}
