package com.fillumina.performance.assertion;

import com.fillumina.performance.infrastructure.AssertableImpl;
import com.fillumina.performance.infrastructure.PHolder;
import com.fillumina.performance.util.stats.Ratio;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AssertPercentageConditionTest {

    @Test(expected = PercentageAssertionError.class)
    public void shouldConsumeAndThrowException() {
        AssertPercentageCondition<AssertableImpl> aoc =
                new AssertPercentageCondition<>("first",
                        EqCondition.EQUALS,
                        Ratio.percentage(23),
                        Ratio.percentage(3));

        AssertableImpl ai = new AssertableImpl(
                "first", 12.3, "second", 45.6, "third", 34.5);

        PHolder<AssertableImpl> holder = new PHolder<>(ai);

        aoc.consume(holder);
        throw new RuntimeException("shouln't be here");
    }

    @Test
    public void shouldConsumeLessThanAndBeOk() {
        AssertPercentageCondition<AssertableImpl> aoc =
                new AssertPercentageCondition<>("first",
                        EqCondition.LESS,
                        Ratio.percentage(30),
                        Ratio.percentage(3));

        AssertableImpl ai = new AssertableImpl(
                "first", 12.3, "second", 45.6, "third", 34.5);

        PHolder<AssertableImpl> holder = new PHolder<>(ai);

        aoc.consume(holder);
    }

    @Test(expected = PercentageAssertionError.class)
    public void shouldConsumeEqualsAndThrowException() {
        AssertPercentageCondition<AssertableImpl> aoc =
                new AssertPercentageCondition<>("first",
                        EqCondition.EQUALS,
                        Ratio.percentage(23),
                        Ratio.percentage(3));

        AssertableImpl ai = new AssertableImpl(
                "first", 12.3, "second", 45.6, "third", 34.5);

        PHolder<AssertableImpl> holder = new PHolder<>(ai);

        aoc.consume(holder);
    }

    public static void main(final String[] args) {
        AssertPercentageCondition<AssertableImpl> aoc =
                new AssertPercentageCondition<>("first",
                        EqCondition.EQUALS,
                        Ratio.percentage(23),
                        Ratio.percentage(3));

        AssertableImpl ai = new AssertableImpl(
                "first", 12.3, "second", 45.6, "third", 34.5);

        PHolder<AssertableImpl> holder = new PHolder<>(ai);

        try {
            aoc.consume(holder);
        } catch(PercentageAssertionError e) {
            System.out.println(e);
        }
    }
}
