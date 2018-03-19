package com.fillumina.performance.util.unit;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MagnitudeTest {

    @Test
    public void shouldReturnMagnitudeMegaName() {
        assertEquals("MEGA", Magnitude.MEGA.name());
    }

    @Test
    public void shouldReturnMagnitudeUnitName() {
        Unit<?> u = Magnitude.MEGA;
        assertEquals("Magnitude", u.getUnitName());
    }

    @Test
    public void shouldReturnMagnitudeUnitNameForMEGA() {
        assertEquals("Magnitude", Magnitude.MEGA.getUnitName());
    }

}
