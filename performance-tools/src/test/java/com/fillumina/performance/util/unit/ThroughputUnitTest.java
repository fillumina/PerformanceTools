package com.fillumina.performance.util.unit;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ThroughputUnitTest {

    @Test
    public void shouldConvert() {
        // 7 op/ns
        Quantity<ThroughputUnit> q = ThroughputUnit.GIGAOP.quantity(7);

        double opsec = q.as(ThroughputUnit.OP);

        assertEquals(7E9, opsec, 0);
    }

    @Test
    public void shouldConvertToBase() {
        // 7 op/ns
        Quantity<ThroughputUnit> q = ThroughputUnit.GIGAOP.quantity(7);

        double opsec = q.toBase();

        assertEquals(7E9, opsec, 0);
    }

    @Test
    public void shouldGetBase() {
        assertEquals(ThroughputUnit.OP, ThroughputUnit.UNITS.getBase());
    }
}
