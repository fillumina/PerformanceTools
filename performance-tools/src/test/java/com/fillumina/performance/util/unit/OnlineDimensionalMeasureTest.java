package com.fillumina.performance.util.unit;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class OnlineDimensionalMeasureTest {

    @Test
    public void shouldUseGivenMilliToPrint() {
        final OnlineDimensionalMeasure odm =
                new OnlineDimensionalMeasure(Magnitude.UNIT, 0.123, 0.124, 0.122);
        assertEquals("123.000 +/- 1.132 (3 samples) m",
                odm.toString(Magnitude.MILLI));
    }

    @Test
    public void shouldUseGivenUnitToPrint() {
        assertEquals("0.123 +/- 0.001 (3 samples) ",
                new OnlineDimensionalMeasure(Magnitude.UNIT, 0.123, 0.124, 0.122)
                        .toString(Magnitude.UNIT)
            );
    }

    @Test
    public void shouldAddQuantity() {
        OnlineDimensionalMeasure m = new OnlineDimensionalMeasure(Magnitude.MILLI);
        m.addSample(Quantity.of(7.8, Magnitude.KILO));

        assertEquals(7_800_000.0, m.getMean(), 0);
    }

    @Test
    public void shouldAddValueAndUnit() {
        OnlineDimensionalMeasure m = new OnlineDimensionalMeasure(Magnitude.MILLI);
        m.addSample(7.8, Magnitude.KILO);

        assertEquals(7_800_000.0, m.getMean(), 0);
    }
}
