package com.fillumina.performance.assertion;

import com.fillumina.performance.mock.AssertableMock;
import com.fillumina.performance.util.RelativeOrder;
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
                        RelativeOrder.EQUALS,
                        Ratio.percentage(23),
                        Ratio.percentage(3));

        AssertableMock assertable = AssertableMock.create(
                "first", 12.3, "second", 45.6, "third", 34.5);

        assertion.check(assertable);
        throw new RuntimeException("shouln't be here");
    }

    @Test
    public void shouldConsumeLessThan() {
        PercentageAssertion assertion =
                new PercentageAssertion(
                        "first",
                        RelativeOrder.LESS,
                        Ratio.percentage(30),
                        Ratio.percentage(3));

        AssertableMock assertable = AssertableMock.create(
                "first", 12.3, "second", 45.6, "third", 34.5);

        assertion.check(assertable);
    }

    @Test
    public void shouldNotConsumeLessThan() {
        PercentageAssertion assertion =
                new PercentageAssertion(
                        "first",
                        RelativeOrder.GREATER,
                        Ratio.percentage(30),
                        Ratio.percentage(3));

        AssertableMock assertable = AssertableMock.create(
                "first", 12.3, "second", 45.6, "third", 34.5);

        new NegateExperimentAssertion(assertion).check(assertable);
    }

    @Test
    public void shouldConsumeGreaterThan() {
        PercentageAssertion assertion =
                new PercentageAssertion(
                        "first",
                        RelativeOrder.GREATER,
                        Ratio.percentage(5),
                        Ratio.percentage(3));

        AssertableMock assertable = AssertableMock.create(
                "first", 12.3, "second", 45.6, "third", 34.5);

        assertion.check(assertable);
    }

    @Test
    public void shouldNotConsumeGreaterThan() {
        PercentageAssertion assertion =
                new PercentageAssertion(
                        "first",
                        RelativeOrder.LESS,
                        Ratio.percentage(5),
                        Ratio.percentage(3));

        AssertableMock assertable = AssertableMock.create(
                "first", 12.3, "second", 45.6, "third", 34.5);

        new NegateExperimentAssertion(assertion).check(assertable);
    }

    @Test
    public void shouldConsumeEqualsTo() {
        PercentageAssertion assertion =
                new PercentageAssertion(
                        "first",
                        RelativeOrder.EQUALS,
                        Ratio.percentage(12.3),
                        Ratio.percentage(3));

        AssertableMock assertable = AssertableMock.create(
                "first", 12.3, "second", 100.0, "third", 34.5);

        assertion.check(assertable);
    }

    @Test
    public void shouldConsumeNotEqualsTo() {
        PercentageAssertion assertion =
                new PercentageAssertion(
                        "first",
                        RelativeOrder.EQUALS,
                        Ratio.percentage(66.6),
                        Ratio.percentage(3));

        AssertableMock assertable = AssertableMock.create(
                "first", 12.3, "second", 100.0, "third", 34.5);

        new NegateExperimentAssertion(assertion).check(assertable);
    }

    @Test(expected = PercentageAssertionError.class)
    public void shouldConsumeEqualsAndThrowException() {
        PercentageAssertion assertion =
                new PercentageAssertion(
                        "first",
                        RelativeOrder.EQUALS,
                        Ratio.percentage(23),
                        Ratio.percentage(3));

        AssertableMock assertable = AssertableMock.create(
                "first", 12.3, "second", 45.6, "third", 34.5);

        assertion.check(assertable);
    }

    public static void main(final String[] args) {
        PercentageAssertion assertion =
                new PercentageAssertion(
                        "first",
                        RelativeOrder.EQUALS,
                        Ratio.percentage(23),
                        Ratio.percentage(3));

        AssertableMock assertable = AssertableMock.create(
                "first", 12.3, "second", 45.6, "third", 34.5);

        try {
            assertion.check(assertable);
        } catch(PercentageAssertionError e) {
            System.out.println(e);
        }
    }
}
