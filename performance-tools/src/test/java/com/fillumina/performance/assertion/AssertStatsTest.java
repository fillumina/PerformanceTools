package com.fillumina.performance.assertion;

import com.fillumina.performance.util.stats.OnlineMeasure;
import com.fillumina.performance.util.stats.Ratio;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AssertStatsTest {

    @Test
    public void shouldCreateWithTolerance() {
        Ratio tolerance = Ratio.percentage(77);
        Assertions assertion =
                Assertions.withTolerance(tolerance);

        assertEquals(tolerance, assertion.getTolerance());
    }

    @Test
    public void shouldAssertPercentage() {
        Ratio tolerance = Ratio.percentage(10);
        Assertions assertion =
                Assertions.withTolerance(tolerance)
                .assertPercentage("half").sameAs(50);

        AssertableMock assertable = AssertableMock.createWithName("test",
                    "half", new OnlineMeasure(50),
                    "full", new OnlineMeasure(100));

        assertion.accept(assertable);
    }

    @Test(expected = PercentageAssertionError.class)
    public void shouldNotAssertPercentage() {
        Ratio tolerance = Ratio.percentage(10);
        Assertions statsAssertion =
                Assertions.withTolerance(tolerance)
                .assertPercentage("half").sameAs(10);

        AssertableMock assertable = AssertableMock.createWithName("test",
                    "half", new OnlineMeasure(50),
                    "full", new OnlineMeasure(100));

        statsAssertion.accept(assertable);
    }

    @Test
    public void shouldAssertOrder() {
        Ratio tolerance = Ratio.percentage(10);
        Assertions statsAssertion =
                Assertions.withTolerance(tolerance)
                .assertOrder("half").lessThan("full");

        AssertableMock assertable = AssertableMock.createWithName("test",
                    "half", new OnlineMeasure(50),
                    "full", new OnlineMeasure(100));

        statsAssertion.accept(assertable);
    }

    @Test(expected = OrderAssertionError.class)
    public void shouldNotAssertOrder() {
        Ratio tolerance = Ratio.percentage(10);
        Assertions statsAssertion =
                Assertions.withTolerance(tolerance)
                .assertOrder("half").greaterThan("full");

        AssertableMock assertable = AssertableMock.createWithName("test",
                    "half", new OnlineMeasure(50),
                    "full", new OnlineMeasure(100));

        statsAssertion.accept(assertable);
    }

    @Test
    public void shouldAssertValue() {
        Ratio tolerance = Ratio.percentage(10);
        Assertions statsAssertion =
                Assertions.withTolerance(tolerance)
                .assertValue("half").sameAs(50);

        AssertableMock assertable = AssertableMock.createWithName("test",
                    "half", new OnlineMeasure(50),
                    "full", new OnlineMeasure(100));

        statsAssertion.accept(assertable);
    }

    @Test(expected = ValueAssertionError.class)
    public void shouldNotAssertValue() {
        Ratio tolerance = Ratio.percentage(10);
        Assertions statsAssertion =
                Assertions.withTolerance(tolerance)
                .assertValue("half").sameAs(78);

        AssertableMock assertable = AssertableMock.createWithName("test",
                    "half", new OnlineMeasure(50),
                    "full", new OnlineMeasure(100));

        statsAssertion.accept(assertable);
    }

    @Test
    public void shouldAddCondition() {
        Ratio tolerance = Ratio.percentage(10);
        Assertions statsAssertion =
                Assertions.withTolerance(tolerance);

        AssertionMock assertion = new AssertionMock();

        statsAssertion.addAssertion(assertion);

        AssertableMock assertable = AssertableMock.createWithName("test",
                    "half", new OnlineMeasure(50),
                    "full", new OnlineMeasure(100));

        statsAssertion.accept(assertable);

        assertEquals("alpha",
                ((AssertableMock)assertion.getConsumedAssertableList().get(0))
                        .getName());
    }

    @Test
    public void shouldSetTolerance() {
        Ratio tolerance = Ratio.percentage(17);

        Assertions statsAssertion = new Assertions();

        statsAssertion.tolerance(tolerance);

        assertEquals(tolerance, statsAssertion.getTolerance());
    }
}
