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
        assertEquals("123.0000 ± 1.1316 (3 samples) ms",
                new DimensionalOnlineMeasure(12.3E7, 12.4E7, 12.2E7)
                        .toString(IntervalUnit.MILLISECONDS)
            );
    }
}
