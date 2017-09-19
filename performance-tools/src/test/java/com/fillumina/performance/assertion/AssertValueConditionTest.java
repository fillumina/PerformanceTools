package com.fillumina.performance.assertion;

import com.fillumina.performance.infrastructure.TN;
import com.fillumina.performance.util.EqCondition;
import com.fillumina.performance.util.stats.Ratio;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AssertValueConditionTest {

    @Test(expected = ValueAssertionError.class)
    public void shouldConsumeAndThrowException() {
        ValueAssertion assertion =
                new ValueAssertion(
                        TN.tname("first"),
                        EqCondition.EQUALS,
                        23,
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
                        TN.tname("first"),
                        EqCondition.EQUALS,
                        11.8,
                        Ratio.percentage(5));

        AssertableMock assertable = AssertableMock.create(
                "first", 12.3, "second", 45.6, "third", 34.5);

        assertion.accept(assertable);
    }

    @Test(expected = ValueAssertionError.class)
    public void shouldConsumeEqualsAndThrowException() {
        ValueAssertion assertion =
                new ValueAssertion(
                        TN.tname("first"),
                        EqCondition.EQUALS,
                        23,
                        Ratio.percentage(3));

        AssertableMock assertable = AssertableMock.create(
                "first", 12.3, "second", 45.6, "third", 34.5);

        assertion.accept(assertable);
    }

    public static void main(final String[] args) {
        ValueAssertion assertion =
                new ValueAssertion(
                        TN.tname("first"),
                        EqCondition.EQUALS,
                        23,
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
