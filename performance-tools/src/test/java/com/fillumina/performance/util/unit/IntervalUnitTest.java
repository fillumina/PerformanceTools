package com.fillumina.performance.util.unit;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class IntervalUnitTest {

    @Test
    public void shouldUseQuantityBuilder() {
        Quantity<IntervalUnit> q = IntervalUnit.quantity().s(2).ms(100).get();
        assertEquals(2.1, q.as(IntervalUnit.SECONDS), 0);
    }
}
