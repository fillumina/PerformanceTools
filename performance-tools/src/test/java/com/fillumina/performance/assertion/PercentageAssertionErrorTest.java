package com.fillumina.performance.assertion;

import com.fillumina.performance.mock.AssertableMock;
import com.fillumina.performance.util.RelativeOrder;
import com.fillumina.performance.util.stats.Ratio;
import java.util.Map;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class PercentageAssertionErrorTest {

    @Test
    public void shouldConsumeEqualsAndThrowException() {
        PercentageAssertion assertion =
                new PercentageAssertion(
                        "first",
                        RelativeOrder.EQUALS,
                        Ratio.percentage(23),
                        Ratio.percentage(3));

        AssertableMock assertable = AssertableMock.create(
                "first", 12.3, "second", 45.6, "third", 34.5);

        try {
            assertion.accept(assertable);
            fail();
        } catch (PercentageAssertionError e) {
            assertEquals("first", e.getTestName());
            assertEquals(23, e.getExpected().getPercentage(),0);
            assertEquals(0.2697368, e.getRatio().getValue(),1E-4);
            assertEquals(3, e.getTolerance().getPercentage(), 0);

            Map<RelativeOrder,Ratio> map = e.getWhatIfToleranceMap();
            assertEquals(18.0, map.get(RelativeOrder.EQUALS).getPercentage(), 0);
            assertEquals(18.0, map.get(RelativeOrder.LESS).getPercentage(), 0);
            assertNull(map.get(RelativeOrder.GREATER));
        }
    }

}
