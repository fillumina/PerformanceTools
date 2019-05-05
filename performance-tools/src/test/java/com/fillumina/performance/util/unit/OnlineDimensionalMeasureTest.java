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
        assertEquals("123.000 +/- 1.132 (3 samples) m",
                new OnlineDimensionalMeasure(0.123, 0.124, 0.122)
                        .toString(Magnitude.MILLI)
            );
    }

    @Test
    public void shouldUseGivenUnitToPrint() {
        assertEquals("0.123 +/- 0.001 (3 samples) ",
                new OnlineDimensionalMeasure(0.123, 0.124, 0.122)
                        .toString(Magnitude.UNIT)
            );
    }
}
