package com.fillumina.performance.util.unit;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class DimensionalOnlineMeasureTest {

    @Test
    public void shouldUseThePassedUnitToPrint() {
        assertEquals("123.000000 ± 1.131581 (3 samples) ms",
                new DimensionalOnlineMeasure(12.3E7, 12.4E7, 12.2E7)
                        .toString(IntervalUnit.MILLISECONDS)
            );
    }
}
