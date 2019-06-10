package com.fillumina.performance.assertion;

import com.fillumina.performance.mock.AssertableMock;
import com.fillumina.performance.mock.AssertionMock;
import com.fillumina.performance.util.stats.Ratio;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AssertionsTest {

    @Test
    public void shouldAssertRatioPercentage() {
        Ratio tolerance = Ratio.percentage(10);
        Assertions assertion =
                Assertions.withTolerance(tolerance)
                .assertRatioPercentage("half").equalsTo(50);

        AssertableMock assertable = AssertableMock.createWithName("test",
                    "half", 50, "full", 100);

        assertion.check(assertable);
    }

    @Test
    public void shouldAssertRatioDecimal() {
        Ratio tolerance = Ratio.percentage(10);
        Assertions assertion =
                Assertions.withTolerance(tolerance)
                .assertRatioDecimal("half").equalsTo(0.5);

        AssertableMock assertable = AssertableMock.createWithName("test",
                    "half", 50, "full", 100);

        assertion.check(assertable);
    }

    @Test
    public void shouldAssertRatio() {
        Ratio tolerance = Ratio.percentage(10);
        Assertions assertion =
                Assertions.withTolerance(tolerance)
                .assertRatio("half").equalsTo(Ratio.percentage(50));

        AssertableMock assertable = AssertableMock.createWithName("test",
                    "half", 50, "full", 100);

        assertion.check(assertable);
    }

    @Test(expected=ExperimentAssertionError.class)
    public void shouldNotAssertPercentage() {
        Ratio tolerance = Ratio.percentage(10);
        Assertions statsAssertion =
                Assertions.withTolerance(tolerance)
                .assertRatioPercentage("half").equalsTo(10);

        AssertableMock assertable = AssertableMock.createWithName("test",
                    "half", 50, "full", 100);

        statsAssertion.check(assertable);
    }

    @Test
    public void shouldAssertOrder() {
        Ratio tolerance = Ratio.percentage(10);
        Assertions statsAssertion =
                Assertions.withTolerance(tolerance)
                .assertOrder("half").lessThan("full");

        AssertableMock assertable = AssertableMock.createWithName("test",
                    "half", 50, "full", 100);

        statsAssertion.check(assertable);
    }

    @Test
    public void shouldAssertOrderGreaterThanOrEquals() {
        Ratio tolerance = Ratio.percentage(10);
        Assertions statsAssertion =
                Assertions.withTolerance(tolerance)
                .assertOrder("full").greaterThanOrEquals("half");

        AssertableMock assertable = AssertableMock.createWithName("test",
                    "half", 50, "full", 100);

        statsAssertion.check(assertable);
    }

    @Test(expected=ExperimentAssertionError.class)
    public void shouldAssertOrderNotGreaterThanOrEquals() {
        Ratio tolerance = Ratio.percentage(10);
        Assertions statsAssertion =
                Assertions.withTolerance(tolerance)
                .assertOrder("half").greaterThanOrEquals("full");

        AssertableMock assertable = AssertableMock.createWithName("test",
                    "half", 50, "full", 100);

        statsAssertion.check(assertable);
    }

    @Test
    public void shouldAssertOrderLessThanOrEquals() {
        Ratio tolerance = Ratio.percentage(10);
        Assertions statsAssertion =
                Assertions.withTolerance(tolerance)
                .assertOrder("half").lessThanOrEquals("full");

        AssertableMock assertable = AssertableMock.createWithName("test",
                    "half", 50, "full", 100);

        statsAssertion.check(assertable);
    }

    @Test(expected=ExperimentAssertionError.class)
    public void shouldAssertOrderNotLessThanOrEquals() {
        Ratio tolerance = Ratio.percentage(10);
        Assertions statsAssertion =
                Assertions.withTolerance(tolerance)
                .assertOrder("full").lessThanOrEquals("half");

        AssertableMock assertable = AssertableMock.createWithName("test",
                    "half", 50, "full", 100);

        statsAssertion.check(assertable);
    }

    @Test
    public void shouldAssertOrderNotEquals() {
        Ratio tolerance = Ratio.percentage(10);
        Assertions statsAssertion =
                Assertions.withTolerance(tolerance)
                .assertOrder("full").notEqualsTo("half");

        AssertableMock assertable = AssertableMock.createWithName("test",
                    "half", 50, "full", 100);

        statsAssertion.check(assertable);
    }

    @Test(expected = ExperimentAssertionError.class)
    public void shouldAssertOrderNotEqualsFailing() {
        Ratio tolerance = Ratio.percentage(10);
        Assertions statsAssertion =
                Assertions.withTolerance(tolerance)
                .assertOrder("full").notEqualsTo("full");

        AssertableMock assertable = AssertableMock.createWithName("test",
                    "half", 50, "full", 100);

        statsAssertion.check(assertable);
    }

    @Test
    public void shouldNotAssertOrder() {
        Ratio tolerance = Ratio.percentage(10);
        Assertions statsAssertion =
                Assertions.withTolerance(tolerance)
                .assertOrder("half").greaterThan("full");

        AssertableMock assertable = AssertableMock.createWithName("test",
                    "half", 50, "full", 100);

        try {
            statsAssertion.check(assertable);
        } catch (ExperimentAssertionError ex) {
            OrderInfo evaluator = (OrderInfo) ex.getInfo();
            assertNotNull(evaluator);
        }
    }

    @Test
    public void shouldAssertValueEquals() {
        Ratio tolerance = Ratio.percentage(10);
        Assertions statsAssertion =
                Assertions.withTolerance(tolerance)
                .assertValue("half").equalsTo(50);

        AssertableMock assertable = AssertableMock.createWithName("test",
                    "half", 50, "full", 100);

        statsAssertion.check(assertable);
    }

    @Test
    public void shouldAssertValueGreaterThan() {
        Ratio tolerance = Ratio.percentage(10);
        Assertions statsAssertion =
                Assertions.withTolerance(tolerance)
                .assertValue("half").greaterThan(20);

        AssertableMock assertable = AssertableMock.createWithName("test",
                    "half", 50, "full", 100);

        statsAssertion.check(assertable);
    }

    @Test
    public void shouldAssertValueLesssThan() {
        Ratio tolerance = Ratio.percentage(10);
        Assertions statsAssertion =
                Assertions.withTolerance(tolerance)
                .assertValue("half").lessThan(80);

        AssertableMock assertable = AssertableMock.createWithName("test",
                    "half", 50, "full", 100);

        statsAssertion.check(assertable);
    }

    @Test
    public void shouldAssertValueNotEquals() {
        Ratio tolerance = Ratio.percentage(10);
        Assertions statsAssertion =
                Assertions.withTolerance(tolerance)
                .assertValue("half").notEqualsTo(90);

        AssertableMock assertable = AssertableMock.createWithName("test",
                    "half", 50, "full", 100);

        statsAssertion.check(assertable);
    }

    @Test
    public void shouldAssertValueLessOrEqualsThan() {
        Ratio tolerance = Ratio.percentage(10);
        Assertions statsAssertion =
                Assertions.withTolerance(tolerance)
                .assertValue("half").lessThanOrEquals(70);

        AssertableMock assertable = AssertableMock.createWithName("test",
                    "half", 50, "full", 100);

        statsAssertion.check(assertable);
    }

    @Test
    public void shouldAssertValuegreaterOrEqualsThan() {
        Ratio tolerance = Ratio.percentage(10);
        Assertions statsAssertion =
                Assertions.withTolerance(tolerance)
                .assertValue("half").greaterThanOrEquals(20);

        AssertableMock assertable = AssertableMock.createWithName("test",
                    "half", 50, "full", 100);

        statsAssertion.check(assertable);
    }

    @Test(expected = ExperimentAssertionError.class)
    public void shouldNotAssertValue() {
        Ratio tolerance = Ratio.percentage(10);
        Assertions statsAssertion =
                Assertions.withTolerance(tolerance)
                .assertValue("half").equalsTo(78);

        AssertableMock assertable = AssertableMock.createWithName("test",
                    "half", 50, "full", 100);

        statsAssertion.check(assertable);
    }

    @Test
    public void shouldAddAssertion() {
        Ratio tolerance = Ratio.percentage(10);
        Assertions statsAssertion =
                Assertions.withTolerance(tolerance);

        AssertionMock assertion = new AssertionMock();

        statsAssertion.accept(assertion);

        AssertableMock assertable = AssertableMock.createWithName("test",
                    "half", 50, "full", 00);

        statsAssertion.check(assertable);

        assertEquals("test",
                ((AssertableMock)assertion.getConsumedAssertableList().get(0))
                        .getName());
    }

}
