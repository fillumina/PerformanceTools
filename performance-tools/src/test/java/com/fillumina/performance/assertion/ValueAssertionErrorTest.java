package com.fillumina.performance.assertion;

import com.fillumina.performance.mock.AssertableMock;
import com.fillumina.performance.util.RelativeOrder;
import com.fillumina.performance.util.stats.Ratio;
import com.fillumina.performance.util.unit.Absolute;
import java.util.Map;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ValueAssertionErrorTest {

    @Test
    public void shouldConsumeEqualsAndThrowException() {
        ValueAssertion assertion =
                new ValueAssertion(
                        "first",
                        RelativeOrder.EQUALS,
                        Absolute.UNIT.quantity(23),
                        Ratio.percentage(3));

        AssertableMock assertable = AssertableMock.create(
                "first", 12.3, "second", 45.6, "third", 34.5);

        try {
            assertion.check(assertable);
            fail();

        } catch (ValueAssertionError e) {
            assertEquals("first", e.getTestName().toString());
            assertEquals(23, e.getExpected().getValue(),0);
            assertEquals(12.3, e.getActualValue().getMean(), 1E-4);
            assertEquals(3, e.getTolerance().getPercentage(), 0);

            Map<RelativeOrder,Ratio> map = e.getWhatIfToleranceMap();
            assertEquals(87.0, map.get(RelativeOrder.EQUALS).getPercentage(), 0);
            assertEquals(87.0, map.get(RelativeOrder.GREATER).getPercentage(), 0);
            assertNull(map.get(RelativeOrder.LESS));
        }
    }

}
