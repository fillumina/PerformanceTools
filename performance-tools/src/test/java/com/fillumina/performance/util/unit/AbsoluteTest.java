package com.fillumina.performance.util.unit;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AbsoluteTest {

    @Test
    public void shouldRepresentTheAbsoluteValue() {
        double value = 123.456;

        Quantity<Absolute> quantity = Absolute.UNIT.quantity(value);

        assertEquals(value, quantity.getValue(), 0);
    }

}
