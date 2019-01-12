package com.fillumina.performance.assertion;

import com.fillumina.performance.mock.AssertableMock;
import com.fillumina.performance.util.RelativeOrder;
import com.fillumina.performance.util.stats.Ratio;
import com.fillumina.performance.util.unit.Absolute;
import com.fillumina.performance.util.unit.IntervalUnit;
import com.fillumina.performance.util.unit.Magnitude;
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

        assertion.accept(assertable);
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

        assertion.accept(assertable);
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

        assertion.accept(assertable);
    }

    @Test
    public void shouldBeEqualsWithDifferentUnits() {
        ValueAssertion assertion =
                new ValueAssertion(
                        "first",
                        RelativeOrder.EQUALS,
                        Magnitude.MILLI.quantity(12.3E3),
                        Ratio.percentage(3));

        AssertableMock assertable = AssertableMock.createWithNameAdUnit("",
                Magnitude.UNIT,
                "first", 12.3, "second", 45.6, "third", 34.5);

        assertion.accept(assertable);
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

        assertion.accept(assertable);
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
            assertion.accept(assertable);
        } catch(ValueAssertionError e) {
            System.out.println(e);
        }
    }
}
