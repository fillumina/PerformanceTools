package com.fillumina.performance.assertion;

import com.fillumina.performance.mock.AssertableMock;
import com.fillumina.performance.util.RelativeOrder;
import com.fillumina.performance.util.stats.Ratio;
import java.util.Map;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class OrderAssertionErrorTest {

    @Test
    public void shouldReturnError() {
        OrderAssertion assertion =
                new OrderAssertion(
                        "first",
                        "second",
                        RelativeOrder.EQUALS,
                        Ratio.percentage(3));

        AssertableMock assertable = AssertableMock.create(
                "first", 12.3, "second", 45.6, "third", 34.5);

        try {
            assertion.accept(assertable);
        } catch(OrderAssertionError e) {

            assertEquals(12.3, e.getFirstMeasure().getMean(), 0);
            assertEquals(45.6, e.getSecondMeasure().getMean(), 0);
            assertEquals("first", e.getFirstTestName());
            assertEquals("second", e.getSecondTestName());
            assertEquals(Ratio.percentage(3), e.getTolerance());

            Map<RelativeOrder,Ratio> map = e.getWhatIfToleranceMap();
            assertEquals(271.0, map.get(RelativeOrder.EQUALS).getPercentage(), 0);
            assertEquals(271.0, map.get(RelativeOrder.GREATER).getPercentage(), 0);
            assertNull(map.get(RelativeOrder.LESS));
        }

    }

    @Test
    public void shouldAllowWhatIfChecks() {
        OrderAssertion assertion =
                new OrderAssertion(
                        "first",
                        "second",
                        RelativeOrder.EQUALS,
                        Ratio.percentage(3));

        AssertableMock assertable = AssertableMock.create(
                "first", 12.3, "second", 45.6, "third", 34.5);

        try {
            assertion.accept(assertable);
            fail();
        } catch(OrderAssertionError e) {
            assertTrue(e.isConditionSatisfied(RelativeOrder.GREATER,
                    Ratio.percentage(271.0)));
        }

    }
}
