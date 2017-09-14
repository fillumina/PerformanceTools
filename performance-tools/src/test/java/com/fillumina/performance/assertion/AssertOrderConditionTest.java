package com.fillumina.performance.assertion;

import com.fillumina.performance.infrastructure.TN;
import com.fillumina.performance.mock.AssertableMock;
import com.fillumina.performance.util.EqCondition;
import com.fillumina.performance.util.stats.Ratio;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AssertOrderConditionTest {

    @Test(expected = OrderAssertionError.class)
    public void shouldConsumeAndThrowException() {
        AssertOrderCondition aoc =
                new AssertOrderCondition(
                        TN.tname("first"),
                        TN.tname("second"),
                        EqCondition.GREATER,
                        Ratio.percentage(3));

        AssertableMock ai = AssertableMock.create(
                "first", 12.3, "second", 45.6, "third", 34.5);


        aoc.accept(ai);
        throw new RuntimeException("shouln't be here");
    }

    @Test
    public void shouldConsumeLessThanAndBeOk() {
        AssertOrderCondition aoc =
                new AssertOrderCondition(
                        TN.tname("first"),
                        TN.tname("second"),
                        EqCondition.LESS,
                        Ratio.percentage(3));

        AssertableMock ai = AssertableMock.create(
                "first", 12.3, "second", 45.6, "third", 34.5);

        aoc.accept(ai);
    }

    @Test(expected = OrderAssertionError.class)
    public void shouldConsumeEqualsAndThrowException() {
        AssertOrderCondition aoc =
                new AssertOrderCondition(
                        TN.tname("first"),
                        TN.tname("second"),
                        EqCondition.EQUALS,
                        Ratio.percentage(3));

        AssertableMock ai = AssertableMock.create(
                "first", 12.3, "second", 45.6, "third", 34.5);

        aoc.accept(ai);
    }

    public static void main(final String[] args) {
        AssertOrderCondition aoc =
                new AssertOrderCondition(
                        TN.tname("first"),
                        TN.tname("second"),
                        EqCondition.EQUALS,
                        Ratio.percentage(3));

        AssertableMock ai = AssertableMock.create(
                "first", 12.3, "second", 45.6, "third", 34.5);

        try {
            aoc.accept(ai);
        } catch(OrderAssertionError e) {
            System.out.println(e);
        }
    }

}
