package com.fillumina.performance.assertion;

import com.fillumina.performance.infrastructure.AssertableImpl;
import com.fillumina.performance.infrastructure.PHolder;
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
public class ValueAssertionErrorTest {

    @Test
    public void shouldConsumeEqualsAndThrowException() {
        AssertValueCondition<AssertableImpl> aoc =
                new AssertValueCondition<>("first",
                        EqCondition.EQUALS,
                        23,
                        Ratio.percentage(3));

        AssertableImpl ai = new AssertableImpl(
                "first", 12.3, "second", 45.6, "third", 34.5);

        PHolder<AssertableImpl> holder = new PHolder<>(ai);

        try {
            aoc.consume(holder);
            fail();

        } catch (ValueAssertionError e) {
            assertEquals("first", e.getTestName());
            assertEquals(23, e.getExpected(),0);
            assertEquals(12.3, e.getActualValue().getMean(), 1E-4);
            assertEquals(3, e.getTolerance().getPercentage(), 0);

            Map<EqCondition,ToleranceRequired> map =
                    e.getWhatIfToleranceMap();
            assertEquals(87.0, map.get(EqCondition.EQUALS).getPercentage(), 0);
            assertEquals(87.0, map.get(EqCondition.GREATER).getPercentage(), 0);
            assertNull(map.get(EqCondition.LESS));
        }
    }

}
