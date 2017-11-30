package com.fillumina.performance.assertion;

import com.fillumina.performance.mock.AssertableMock;
import com.fillumina.performance.util.EqCondition;
import com.fillumina.performance.util.stats.Ratio;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class PercentageAssertionTest {

    @Test(expected = PercentageAssertionError.class)
    public void shouldConsumeAndThrowException() {
        PercentageAssertion assertion =
                new PercentageAssertion(
                        "first",
                        EqCondition.EQUALS,
                        Ratio.percentage(23),
                        Ratio.percentage(3));

        AssertableMock assertable = AssertableMock.create(
                "first", 12.3, "second", 45.6, "third", 34.5);

        assertion.accept(assertable);
        throw new RuntimeException("shouln't be here");
    }

    @Test
    public void shouldConsumeLessThanAndBeOk() {
        PercentageAssertion assertion =
                new PercentageAssertion(
                        "first",
                        EqCondition.LESS,
                        Ratio.percentage(30),
                        Ratio.percentage(3));

        AssertableMock assertable = AssertableMock.create(
                "first", 12.3, "second", 45.6, "third", 34.5);

        assertion.accept(assertable);
    }

    @Test(expected = PercentageAssertionError.class)
    public void shouldConsumeEqualsAndThrowException() {
        PercentageAssertion assertion =
                new PercentageAssertion(
                        "first",
                        EqCondition.EQUALS,
                        Ratio.percentage(23),
                        Ratio.percentage(3));

        AssertableMock assertable = AssertableMock.create(
                "first", 12.3, "second", 45.6, "third", 34.5);

        assertion.accept(assertable);
    }

    public static void main(final String[] args) {
        PercentageAssertion assertion =
                new PercentageAssertion(
                        "first",
                        EqCondition.EQUALS,
                        Ratio.percentage(23),
                        Ratio.percentage(3));

        AssertableMock assertable = AssertableMock.create(
                "first", 12.3, "second", 45.6, "third", 34.5);

        try {
            assertion.accept(assertable);
        } catch(PercentageAssertionError e) {
            System.out.println(e);
        }
    }
}
