package com.fillumina.performance.assertion;

import com.fillumina.performance.infrastructure.TN;
import com.fillumina.performance.mock.AssertableMock;
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
        AssertOrderCondition<AssertableMock> aoc =
                new AssertOrderCondition<>(
                        TN.n("first"),
                        TN.n("second"),
                        EqCondition.EQUALS,
                        Ratio.percentage(3));

        AssertableMock ai = AssertableMock.create(
                "first", 12.3, "second", 45.6, "third", 34.5);

        try {
            aoc.consume(TN.EMPTY, ai);
        } catch(OrderAssertionError e) {

            assertEquals(12.3, e.getFirstMeasure().getMean(), 0);
            assertEquals(45.6, e.getSecondMeasure().getMean(), 0);
            assertEquals("first", e.getFirstTestName().toString());
            assertEquals("second", e.getSecondTestName().toString());
            assertEquals(Ratio.percentage(3), e.getTolerance());

            Map<EqCondition,ToleranceRequired> map =
                    e.getWhatIfToleranceMap();
            assertEquals(271.0, map.get(EqCondition.EQUALS).getPercentage(), 0);
            assertEquals(271.0, map.get(EqCondition.GREATER).getPercentage(), 0);
            assertNull(map.get(EqCondition.LESS));
        }

    }

    @Test
    public void shouldAllowWhatIfChecks() {
        AssertOrderCondition<AssertableMock> aoc =
                new AssertOrderCondition<>(
                        TN.n("first"),
                        TN.n("second"),
                        EqCondition.EQUALS,
                        Ratio.percentage(3));

        AssertableMock ai = AssertableMock.create(
                "first", 12.3, "second", 45.6, "third", 34.5);

        try {
            aoc.consume(TN.EMPTY, ai);
            fail();
        } catch(OrderAssertionError e) {
            assertTrue(e.isConditionSatisfied(
                    EqCondition.GREATER,
                    Ratio.percentage(271.0)));
        }

    }
}
