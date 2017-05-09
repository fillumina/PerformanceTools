package com.fillumina.performance.assertion;

import com.fillumina.performance.util.EqCondition;
import com.fillumina.performance.infrastructure.TN;
import com.fillumina.performance.mock.AssertableMock;
import com.fillumina.performance.util.stats.Ratio;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AssertPercentageConditionTest {

    @Test(expected = PercentageAssertionError.class)
    public void shouldConsumeAndThrowException() {
        AssertPercentageCondition<AssertableMock> aoc =
                new AssertPercentageCondition<>(
                        TN.tname("first"),
                        EqCondition.EQUALS,
                        Ratio.percentage(23),
                        Ratio.percentage(3));

        AssertableMock ai = AssertableMock.create(
                "first", 12.3, "second", 45.6, "third", 34.5);

        aoc.consume(TN.EMPTY, ai);
        throw new RuntimeException("shouln't be here");
    }

    @Test
    public void shouldConsumeLessThanAndBeOk() {
        AssertPercentageCondition<AssertableMock> aoc =
                new AssertPercentageCondition<>(
                        TN.tname("first"),
                        EqCondition.LESS,
                        Ratio.percentage(30),
                        Ratio.percentage(3));

        AssertableMock ai = AssertableMock.create(
                "first", 12.3, "second", 45.6, "third", 34.5);

        aoc.consume(TN.EMPTY, ai);
    }

    @Test(expected = PercentageAssertionError.class)
    public void shouldConsumeEqualsAndThrowException() {
        AssertPercentageCondition<AssertableMock> aoc =
                new AssertPercentageCondition<>(
                        TN.tname("first"),
                        EqCondition.EQUALS,
                        Ratio.percentage(23),
                        Ratio.percentage(3));

        AssertableMock ai = AssertableMock.create(
                "first", 12.3, "second", 45.6, "third", 34.5);

        aoc.consume(TN.EMPTY, ai);
    }

    public static void main(final String[] args) {
        AssertPercentageCondition<AssertableMock> aoc =
                new AssertPercentageCondition<>(
                        TN.tname("first"),
                        EqCondition.EQUALS,
                        Ratio.percentage(23),
                        Ratio.percentage(3));

        AssertableMock ai = AssertableMock.create(
                "first", 12.3, "second", 45.6, "third", 34.5);

        try {
            aoc.consume(TN.EMPTY, ai);
        } catch(PercentageAssertionError e) {
            System.out.println(e);
        }
    }
}
