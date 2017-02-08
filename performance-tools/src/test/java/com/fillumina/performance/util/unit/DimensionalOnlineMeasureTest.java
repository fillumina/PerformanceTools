package com.fillumina.performance.util.unit;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class DimensionalOnlineMeasureTest {

    @Test
    public void shouldUseMsToPrint() {
        assertEquals("123.000000 ± 1.131581 (3 samples) ms",
                new DimensionalOnlineMeasure(12.3E7, 12.4E7, 12.2E7)
                        .toString(IntervalUnit.MILLISECONDS)
            );
    }

    @Test
    public void shouldUseSecToPrint() {
        assertEquals("0.123000 ± 0.001132 (3 samples) s",
                new DimensionalOnlineMeasure(12.3E7, 12.4E7, 12.2E7)
                        .toString(IntervalUnit.SECONDS)
            );
    }
}
