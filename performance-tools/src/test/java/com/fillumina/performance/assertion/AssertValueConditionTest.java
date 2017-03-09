package com.fillumina.performance.assertion;

import com.fillumina.performance.infrastructure.AssertableImpl;
import com.fillumina.performance.infrastructure.PHolder;
import com.fillumina.performance.util.stats.Ratio;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AssertValueConditionTest {

    @Test(expected = ValueAssertionError.class)
    public void shouldConsumeAndThrowException() {
        AssertValueCondition<AssertableImpl> aoc =
                new AssertValueCondition<>("first",
                        EqCondition.EQUALS,
                        23,
                        Ratio.percentage(3));

        AssertableImpl ai = new AssertableImpl(
                "first", 12.3, "second", 45.6, "third", 34.5);

        PHolder<AssertableImpl> holder = new PHolder<>(ai);

        aoc.consume(holder);
        throw new RuntimeException("shouln't be here");
    }

    @Test
    public void shouldConsumeLessThanAndBeOk() {
        AssertValueCondition<AssertableImpl> aoc =
                new AssertValueCondition<>("first",
                        EqCondition.EQUALS,
                        11.8,
                        Ratio.percentage(5));

        AssertableImpl ai = new AssertableImpl(
                "first", 12.3, "second", 45.6, "third", 34.5);

        PHolder<AssertableImpl> holder = new PHolder<>(ai);

        aoc.consume(holder);
    }

    @Test(expected = ValueAssertionError.class)
    public void shouldConsumeEqualsAndThrowException() {
        AssertValueCondition<AssertableImpl> aoc =
                new AssertValueCondition<>("first",
                        EqCondition.EQUALS,
                        23,
                        Ratio.percentage(3));

        AssertableImpl ai = new AssertableImpl(
                "first", 12.3, "second", 45.6, "third", 34.5);

        PHolder<AssertableImpl> holder = new PHolder<>(ai);

        aoc.consume(holder);
    }

    public static void main(final String[] args) {
        AssertValueCondition<AssertableImpl> aoc =
                new AssertValueCondition<>("first",
                        EqCondition.EQUALS,
                        23,
                        Ratio.percentage(3));

        AssertableImpl ai = new AssertableImpl(
                "first", 12.3, "second", 45.6, "third", 34.5);

        PHolder<AssertableImpl> holder = new PHolder<>(ai);

        try {
            aoc.consume(holder);
        } catch(ValueAssertionError e) {
            System.out.println(e);
        }
    }
}
