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
public class AssertValueConditionTest {

    @Test(expected = ValueAssertionError.class)
    public void shouldConsumeAndThrowException() {
        AssertValueCondition aoc =
                new AssertValueCondition(
                        TN.tname("first"),
                        EqCondition.EQUALS,
                        23,
                        Ratio.percentage(3));

        AssertableMock ai = AssertableMock.create(
                "first", 12.3, "second", 45.6, "third", 34.5);

        aoc.accept(ai);
        throw new RuntimeException("shouln't be here");
    }

    @Test
    public void shouldConsumeLessThanAndBeOk() {
        AssertValueCondition aoc =
                new AssertValueCondition(
                        TN.tname("first"),
                        EqCondition.EQUALS,
                        11.8,
                        Ratio.percentage(5));

        AssertableMock ai = AssertableMock.create(
                "first", 12.3, "second", 45.6, "third", 34.5);

        aoc.accept(ai);
    }

    @Test(expected = ValueAssertionError.class)
    public void shouldConsumeEqualsAndThrowException() {
        AssertValueCondition aoc =
                new AssertValueCondition(
                        TN.tname("first"),
                        EqCondition.EQUALS,
                        23,
                        Ratio.percentage(3));

        AssertableMock ai = AssertableMock.create(
                "first", 12.3, "second", 45.6, "third", 34.5);

        aoc.accept(ai);
    }

    public static void main(final String[] args) {
        AssertValueCondition aoc =
                new AssertValueCondition(
                        TN.tname("first"),
                        EqCondition.EQUALS,
                        23,
                        Ratio.percentage(3));

        AssertableMock ai = AssertableMock.create(
                "first", 12.3, "second", 45.6, "third", 34.5);

        try {
            aoc.accept(ai);
        } catch(ValueAssertionError e) {
            System.out.println(e);
        }
    }
}
