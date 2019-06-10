package com.fillumina.performance.assertion;

import com.fillumina.performance.mock.AssertableMock;
import com.fillumina.performance.util.stats.Ratio;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class PercentageInfoTest {

    @Test(expected = ExperimentAssertionError.class)
    public void shouldConsumeAndThrowException() {
        ExperimentAssertion assertion =
                Assertions.withTolerance(Ratio.percentage(3))
                        .assertRatioPercentage("first")
                        .equalsTo(23);

        AssertableMock assertable = AssertableMock.create(
                "first", 12.3, "second", 45.6, "third", 34.5);

        assertion.check(assertable);
        throw new RuntimeException("shouln't be here");
    }

    @Test
    public void shouldConsumeLessThan() {
        ExperimentAssertion assertion =
                Assertions.withTolerance(Ratio.percentage(3))
                        .assertRatioPercentage("first")
                        .lessThan(30);

        AssertableMock assertable = AssertableMock.create(
                "first", 12.3, "second", 45.6, "third", 34.5);

        assertion.check(assertable);
    }

    @Test
    public void shouldNotConsumeLessThan() {
        ExperimentAssertion assertion =
                Assertions.withTolerance(Ratio.percentage(3))
                        .assertRatioPercentage("first")
                        .lessThan(3);

        AssertableMock assertable = AssertableMock.create(
                "first", 12.3, "second", 45.6, "third", 34.5);

        assertFalse(assertion.satisfy(assertable));
    }

    @Test
    public void shouldConsumeGreaterThan() {
        ExperimentAssertion assertion =
                Assertions.withTolerance(Ratio.percentage(3))
                        .assertRatioPercentage("first")
                        .greaterThan(3);

        AssertableMock assertable = AssertableMock.create(
                "first", 12.3, "second", 45.6, "third", 34.5);

        assertion.check(assertable);
    }

    @Test
    public void shouldNotConsumeGreaterThan() {
        ExperimentAssertion assertion =
                Assertions.withTolerance(Ratio.percentage(3))
                        .assertRatioPercentage("first")
                        .lessThan(5);

        AssertableMock assertable = AssertableMock.create(
                "first", 12.3, "second", 45.6, "third", 34.5);

        assertFalse(assertion.satisfy(assertable));
    }

    @Test
    public void shouldConsumeEqualsTo() {
        ExperimentAssertion assertion =
                Assertions.withTolerance(Ratio.percentage(3))
                        .assertRatioPercentage("first")
                        .equalsTo(12.3);

        AssertableMock assertable = AssertableMock.create(
                "first", 12.3, "second", 100.0, "third", 34.5);

        assertion.check(assertable);
    }

    @Test
    public void shouldConsumeNotEqualsTo() {
        ExperimentAssertion assertion =
                Assertions.withTolerance(Ratio.percentage(3))
                        .assertRatioPercentage("first")
                        .equalsTo(66.6);

        AssertableMock assertable = AssertableMock.create(
                "first", 12.3, "second", 100.0, "third", 34.5);

        assertFalse(assertion.satisfy(assertable));
    }

    @Test(expected = ExperimentAssertionError.class)
    public void shouldConsumeEqualsAndThrowException() {
        ExperimentAssertion assertion =
                Assertions.withTolerance(Ratio.percentage(3))
                        .assertRatioPercentage("first")
                        .equalsTo(23);

        AssertableMock assertable = AssertableMock.create(
                "first", 12.3, "second", 45.6, "third", 34.5);

        assertion.check(assertable);
    }

    public static void main(final String[] args) {
        ExperimentAssertion assertion =
                Assertions.withTolerance(Ratio.percentage(3))
                        .assertRatioPercentage("first")
                        .equalsTo(23);

        AssertableMock assertable = AssertableMock.create(
                "first", 12.3, "second", 45.6, "third", 34.5);

        try {
            assertion.check(assertable);
        } catch(ExperimentAssertionError e) {
            System.out.println(e);
        }
    }
}
