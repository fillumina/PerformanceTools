package com.fillumina.performance.assertion;

import com.fillumina.performance.infrastructure.AssertableImpl;
import com.fillumina.performance.infrastructure.PHolder;
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
    public void shouldSetTolerance() {
        Ratio tolerance = Ratio.decimal(1.234);
        StatsAssertion<Void, Assertable> sa =
                AssertStats.<Assertable>withTolerance(tolerance);

        // just so we can obtain back AssertStats without forcing a conversion
        AssertStats<Void,Assertable> as =
                sa.assertOrder("first").greaterThan("second");

        assertEquals(tolerance, as.getTolerance());
    }

    @Test
    public void shouldAssertPercentage() {
        Ratio tolerance = Ratio.decimal(1.234);
        StatsAssertion<Void, AssertableImpl> statsAssertion =
                AssertStats.<AssertableImpl>withTolerance(tolerance)
                .assertPercentage("half").sameAs(50);

        AssertableImpl assertable = new AssertableImpl("test",
            LinkedMap.<String,OnlineMeasure>create(
                    "half", new OnlineMeasure(50),
                    "full", new OnlineMeasure(100)
            ));

        statsAssertion.consume(new PHolder<>(assertable));
    }

    @Test
    public void testAssertOrder() {
        Ratio tolerance = Ratio.decimal(1.234);
        StatsAssertion<Void, AssertableImpl> statsAssertion =
                AssertStats.<AssertableImpl>withTolerance(tolerance)
                .assertOrder("half").lessThan("full");

        AssertableImpl assertable = new AssertableImpl("test",
            LinkedMap.<String,OnlineMeasure>create(
                    "half", new OnlineMeasure(50),
                    "full", new OnlineMeasure(100)
            ));

        statsAssertion.consume(new PHolder<>(assertable));
    }

    @Test
    public void testAssertValue() {
        Ratio tolerance = Ratio.decimal(1.234);
        StatsAssertion<Void, AssertableImpl> statsAssertion =
                AssertStats.<AssertableImpl>withTolerance(tolerance)
                .assertValue("half").sameAs(50);

        AssertableImpl assertable = new AssertableImpl("test",
            LinkedMap.<String,OnlineMeasure>create(
                    "half", new OnlineMeasure(50),
                    "full", new OnlineMeasure(100)
            ));

        statsAssertion.consume(new PHolder<>(assertable));
    }

    @Test
    public void testAddCondition() {
    }

    @Test
    public void testCheck() {
    }

    @Test
    public void testConsume() {
    }

    @Test
    public void testSetTolerance() {
    }

    @Test
    public void testGetTolerance() {
    }

    @Test
    public void testToString() {
    }

}
