package com.fillumina.performance.assertion;

import com.fillumina.performance.infrastructure.PHolder;
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
                new AssertPercentageCondition<>("first",
                        EqCondition.EQUALS,
                        Ratio.percentage(23),
                        Ratio.percentage(3));

        AssertableMock ai = AssertableMock.create(
                "first", 12.3, "second", 45.6, "third", 34.5);

        PHolder<AssertableMock> holder = new PHolder<>(ai);

        aoc.consume(holder);
        throw new RuntimeException("shouln't be here");
    }

    @Test
    public void shouldConsumeLessThanAndBeOk() {
        AssertPercentageCondition<AssertableMock> aoc =
                new AssertPercentageCondition<>("first",
                        EqCondition.LESS,
                        Ratio.percentage(30),
                        Ratio.percentage(3));

        AssertableMock ai = AssertableMock.create(
                "first", 12.3, "second", 45.6, "third", 34.5);

        PHolder<AssertableMock> holder = new PHolder<>(ai);

        aoc.consume(holder);
    }

    @Test(expected = PercentageAssertionError.class)
    public void shouldConsumeEqualsAndThrowException() {
        AssertPercentageCondition<AssertableMock> aoc =
                new AssertPercentageCondition<>("first",
                        EqCondition.EQUALS,
                        Ratio.percentage(23),
                        Ratio.percentage(3));

        AssertableMock ai = AssertableMock.create(
                "first", 12.3, "second", 45.6, "third", 34.5);

        PHolder<AssertableMock> holder = new PHolder<>(ai);

        aoc.consume(holder);
    }

    public static void main(final String[] args) {
        AssertPercentageCondition<AssertableMock> aoc =
                new AssertPercentageCondition<>("first",
                        EqCondition.EQUALS,
                        Ratio.percentage(23),
                        Ratio.percentage(3));

        AssertableMock ai = AssertableMock.create(
                "first", 12.3, "second", 45.6, "third", 34.5);

        PHolder<AssertableMock> holder = new PHolder<>(ai);

        try {
            aoc.consume(holder);
        } catch(PercentageAssertionError e) {
            System.out.println(e);
        }
    }
}
