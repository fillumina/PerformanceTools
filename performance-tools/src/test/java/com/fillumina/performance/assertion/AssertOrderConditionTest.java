package com.fillumina.performance.assertion;

import com.fillumina.performance.infrastructure.AssertableImpl;
import com.fillumina.performance.infrastructure.PHolder;
import com.fillumina.performance.util.stats.Ratio;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AssertOrderConditionTest {

    @Test(expected = OrderAssertionError.class)
    public void shouldConsumeAndThrowException() {
        AssertOrderCondition<AssertableImpl> aoc =
                new AssertOrderCondition<>("first", "second",
                        EqCondition.GREATER,
                        Ratio.percentage(3));

        AssertableImpl ai = new AssertableImpl(
                "first", 12.3, "second", 45.6, "third", 34.5);

        PHolder<AssertableImpl> holder = new PHolder<>(ai);

        aoc.consume(holder);
        throw new RuntimeException("shouln't be here");
    }

    @Test
    public void shouldConsumeLessThanAndBeOk() {
        AssertOrderCondition<AssertableImpl> aoc =
                new AssertOrderCondition<>("first", "second",
                        EqCondition.LESS,
                        Ratio.percentage(3));

        AssertableImpl ai = new AssertableImpl(
                "first", 12.3, "second", 45.6, "third", 34.5);

        PHolder<AssertableImpl> holder = new PHolder<>(ai);

        aoc.consume(holder);
    }

    @Test(expected = OrderAssertionError.class)
    public void shouldConsumeEqualsAndThrowException() {
        AssertOrderCondition<AssertableImpl> aoc =
                new AssertOrderCondition<>("first", "second",
                        EqCondition.EQUALS,
                        Ratio.percentage(3));

        AssertableImpl ai = new AssertableImpl(
                "first", 12.3, "second", 45.6, "third", 34.5);

        PHolder<AssertableImpl> holder = new PHolder<>(ai);

        aoc.consume(holder);
    }

    public static void main(final String[] args) {
        AssertOrderCondition<AssertableImpl> aoc =
                new AssertOrderCondition<>("first", "second",
                        EqCondition.EQUALS,
                        Ratio.percentage(3));

        AssertableImpl ai = new AssertableImpl(
                "first", 12.3, "second", 45.6, "third", 34.5);

        PHolder<AssertableImpl> holder = new PHolder<>(ai);

        try {
            aoc.consume(holder);
        } catch(OrderAssertionError e) {
            System.out.println(e);
        }
    }

}
