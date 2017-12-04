package com.fillumina.performance.assertion;

import com.fillumina.performance.mock.AssertableMock;
import com.fillumina.performance.util.RelativeOrder;
import com.fillumina.performance.util.stats.Ratio;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class OrderAssertionTest {

    @Test(expected = OrderAssertionError.class)
    public void shouldConsumeAndThrowException() {
        OrderAssertion assertion =
                new OrderAssertion("first", "second",
                        RelativeOrder.GREATER,
                        Ratio.percentage(3));

        AssertableMock assertable = AssertableMock.create(
                "first", 12.3, "second", 45.6, "third", 34.5);


        assertion.accept(assertable);
        throw new RuntimeException("shouln't be here");
    }

    @Test
    public void shouldConsumeLessThanAndBeOk() {
        OrderAssertion assertion =
                new OrderAssertion("first", "second",
                        RelativeOrder.LESS,
                        Ratio.percentage(3));

        AssertableMock assertable = AssertableMock.create(
                "first", 12.3, "second", 45.6, "third", 34.5);

        assertion.accept(assertable);
    }

    @Test(expected = OrderAssertionError.class)
    public void shouldConsumeEqualsAndThrowException() {
        OrderAssertion assertion =
                new OrderAssertion("first", "second",
                        RelativeOrder.EQUALS,
                        Ratio.percentage(3));

        AssertableMock assertable = AssertableMock.create(
                "first", 12.3, "second", 45.6, "third", 34.5);

        assertion.accept(assertable);
    }

    public static void main(final String[] args) {
        OrderAssertion assertion =
                new OrderAssertion("first", "second",
                        RelativeOrder.EQUALS,
                        Ratio.percentage(3));

        AssertableMock assertable = AssertableMock.create(
                "first", 12.3, "second", 45.6, "third", 34.5);

        try {
            assertion.accept(assertable);
        } catch(OrderAssertionError e) {
            System.out.println(e);
        }
    }

}
