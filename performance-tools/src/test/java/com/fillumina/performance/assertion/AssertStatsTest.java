package com.fillumina.performance.assertion;

import com.fillumina.performance.infrastructure.PHolder;
import com.fillumina.performance.mock.AssertableMock;
import com.fillumina.performance.mock.AssertionMock;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.OnlineMeasure;
import com.fillumina.performance.util.stats.Ratio;
import com.fillumina.performance.util.tree.LinkedMap;
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
        AssertStats<Void, AssertableMock> statsAssertion =
                (AssertStats<Void, AssertableMock>)
                AssertStats.<AssertableMock>withTolerance(tolerance);

        assertEquals(tolerance, statsAssertion.getTolerance());
    }

    @Test
    public void shouldAssertPercentage() {
        Ratio tolerance = Ratio.percentage(10);
        StatsAssertion<Void, AssertableMock> statsAssertion =
                AssertStats.<AssertableMock>withTolerance(tolerance)
                .assertPercentage("half").sameAs(50);

        AssertableMock assertable = new AssertableMock("test",
            LinkedMap.<String,Measure>create(
                    "half", new OnlineMeasure(50),
                    "full", new OnlineMeasure(100)
            ));

        statsAssertion.consume(new PHolder<>(assertable));
    }

    @Test(expected = PercentageAssertionError.class)
    public void shouldNotAssertPercentage() {
        Ratio tolerance = Ratio.percentage(10);
        StatsAssertion<Void, AssertableMock> statsAssertion =
                AssertStats.<AssertableMock>withTolerance(tolerance)
                .assertPercentage("half").sameAs(10);

        AssertableMock assertable = new AssertableMock("test",
            LinkedMap.<String,Measure>create(
                    "half", new OnlineMeasure(50),
                    "full", new OnlineMeasure(100)
            ));

        statsAssertion.consume(new PHolder<>(assertable));
    }

    @Test
    public void shouldAssertOrder() {
        Ratio tolerance = Ratio.percentage(10);
        StatsAssertion<Void, AssertableMock> statsAssertion =
                AssertStats.<AssertableMock>withTolerance(tolerance)
                .assertOrder("half").lessThan("full");

        AssertableMock assertable = new AssertableMock("test",
            LinkedMap.<String,Measure>create(
                    "half", new OnlineMeasure(50),
                    "full", new OnlineMeasure(100)
            ));

        statsAssertion.consume(new PHolder<>(assertable));
    }

    @Test(expected = OrderAssertionError.class)
    public void shouldNotAssertOrder() {
        Ratio tolerance = Ratio.percentage(10);
        StatsAssertion<Void, AssertableMock> statsAssertion =
                AssertStats.<AssertableMock>withTolerance(tolerance)
                .assertOrder("half").greaterThan("full");

        AssertableMock assertable = new AssertableMock("test",
            LinkedMap.<String,Measure>create(
                    "half", new OnlineMeasure(50),
                    "full", new OnlineMeasure(100)
            ));

        statsAssertion.consume(new PHolder<>(assertable));
    }

    @Test
    public void shouldAssertValue() {
        Ratio tolerance = Ratio.percentage(10);
        StatsAssertion<Void, AssertableMock> statsAssertion =
                AssertStats.<AssertableMock>withTolerance(tolerance)
                .assertValue("half").sameAs(50);

        AssertableMock assertable = new AssertableMock("test",
            LinkedMap.<String,Measure>create(
                    "half", new OnlineMeasure(50),
                    "full", new OnlineMeasure(100)
            ));

        statsAssertion.consume(new PHolder<>(assertable));
    }

    @Test(expected = ValueAssertionError.class)
    public void shouldNotAssertValue() {
        Ratio tolerance = Ratio.percentage(10);
        StatsAssertion<Void, AssertableMock> statsAssertion =
                AssertStats.<AssertableMock>withTolerance(tolerance)
                .assertValue("half").sameAs(78);

        AssertableMock assertable = new AssertableMock("test",
            LinkedMap.<String,Measure>create(
                    "half", new OnlineMeasure(50),
                    "full", new OnlineMeasure(100)
            ));

        statsAssertion.consume(new PHolder<>(assertable));
    }

    @Test
    public void shouldAddCondition() {
        Ratio tolerance = Ratio.percentage(10);
        AssertStats<Void, AssertableMock> statsAssertion =
                (AssertStats<Void, AssertableMock>)
                AssertStats.<AssertableMock>withTolerance(tolerance);

        AssertionMock<AssertableMock> assertion = new AssertionMock<>();

        statsAssertion.addAssertion(assertion);

        AssertableMock assertable = new AssertableMock("alpha",
            LinkedMap.<String,Measure>create(
                    "half", new OnlineMeasure(50),
                    "full", new OnlineMeasure(100)
            ));

        statsAssertion.check(new PHolder<>(assertable));

        assertEquals("alpha", assertion.getList().get(0));
    }

    @Test
    public void shouldSetTolerance() {
        Ratio tolerance = Ratio.percentage(17);

        AssertStats<Void, AssertableMock> statsAssertion = new AssertStats<>();

        statsAssertion.setTolerance(tolerance);

        assertEquals(tolerance, statsAssertion.getTolerance());
    }
}
