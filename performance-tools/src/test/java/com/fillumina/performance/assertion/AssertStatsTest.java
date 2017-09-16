package com.fillumina.performance.assertion;

import com.fillumina.performance.infrastructure.TN;
import com.fillumina.performance.mock.AssertableMock;
import com.fillumina.performance.mock.AssertionMock;
import com.fillumina.performance.util.tname.TName;
import com.fillumina.performance.util.collection.LinkedMap;
import com.fillumina.performance.util.stats.Measure;
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
        AssertionChecker statsAssertion =
                AssertionChecker.withTolerance(tolerance);

        assertEquals(tolerance, statsAssertion.getTolerance());
    }

    @Test
    public void shouldAssertPercentage() {
        Ratio tolerance = Ratio.percentage(10);
        AssertionChecker statsAssertion =
                AssertionChecker.withTolerance(tolerance)
                .assertPercentage("half").sameAs(50);

        AssertableMock assertable = new AssertableMock("test",
            LinkedMap.<TName,Measure>create(TN.tname("half"), new OnlineMeasure(50),
                    TN.tname("full"), new OnlineMeasure(100)
            ));

        statsAssertion.accept(assertable);
    }

    @Test(expected = PercentageAssertionError.class)
    public void shouldNotAssertPercentage() {
        Ratio tolerance = Ratio.percentage(10);
        AssertionChecker statsAssertion =
                AssertionChecker.withTolerance(tolerance)
                .assertPercentage("half").sameAs(10);

        AssertableMock assertable = new AssertableMock("test",
            LinkedMap.<TName,Measure>create(TN.tname("half"), new OnlineMeasure(50),
                    TN.tname("full"), new OnlineMeasure(100)
            ));

        statsAssertion.accept(assertable);
    }

    @Test
    public void shouldAssertOrder() {
        Ratio tolerance = Ratio.percentage(10);
        AssertionChecker statsAssertion =
                AssertionChecker.withTolerance(tolerance)
                .assertOrder("half").lessThan("full");

        AssertableMock assertable = new AssertableMock("test",
            LinkedMap.<TName,Measure>create(TN.tname("half"), new OnlineMeasure(50),
                    TN.tname("full"), new OnlineMeasure(100)
            ));

        statsAssertion.accept(assertable);
    }

    @Test(expected = OrderAssertionError.class)
    public void shouldNotAssertOrder() {
        Ratio tolerance = Ratio.percentage(10);
        AssertionChecker statsAssertion =
                AssertionChecker.withTolerance(tolerance)
                .assertOrder("half").greaterThan("full");

        AssertableMock assertable = new AssertableMock("test",
            LinkedMap.<TName,Measure>create(TN.tname("half"), new OnlineMeasure(50),
                    TN.tname("full"), new OnlineMeasure(100)
            ));

        statsAssertion.accept(assertable);
    }

    @Test
    public void shouldAssertValue() {
        Ratio tolerance = Ratio.percentage(10);
        AssertionChecker statsAssertion =
                AssertionChecker.withTolerance(tolerance)
                .assertValue("half").sameAs(50);

        AssertableMock assertable = new AssertableMock("test",
            LinkedMap.<TName,Measure>create(TN.tname("half"), new OnlineMeasure(50),
                    TN.tname("full"), new OnlineMeasure(100)
            ));

        statsAssertion.accept(assertable);
    }

    @Test(expected = ValueAssertionError.class)
    public void shouldNotAssertValue() {
        Ratio tolerance = Ratio.percentage(10);
        AssertionChecker statsAssertion =
                AssertionChecker.withTolerance(tolerance)
                .assertValue("half").sameAs(78);

        AssertableMock assertable = new AssertableMock("test",
            LinkedMap.<TName,Measure>create(TN.tname("half"), new OnlineMeasure(50),
                    TN.tname("full"), new OnlineMeasure(100)
            ));

        statsAssertion.accept(assertable);
    }

    @Test
    public void shouldAddCondition() {
        Ratio tolerance = Ratio.percentage(10);
        AssertionChecker statsAssertion =
                AssertionChecker.withTolerance(tolerance);

        AssertionMock assertion = new AssertionMock();

        statsAssertion.addAssertion(assertion);

        AssertableMock assertable = new AssertableMock("alpha",
            LinkedMap.<TName,Measure>create(
                    TN.tname("half"), new OnlineMeasure(50),
                    TN.tname("full"), new OnlineMeasure(100)
            ));

        statsAssertion.accept(assertable);

        assertEquals("alpha",
                ((AssertableMock)assertion.getConsumedAssertableList().get(0))
                        .getName());
    }

    @Test
    public void shouldSetTolerance() {
        Ratio tolerance = Ratio.percentage(17);

        AssertionChecker statsAssertion = new AssertionChecker();

        statsAssertion.tolerance(tolerance);

        assertEquals(tolerance, statsAssertion.getTolerance());
    }
}
