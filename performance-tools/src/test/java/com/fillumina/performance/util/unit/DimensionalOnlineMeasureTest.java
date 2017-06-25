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
        assertEquals("123.000 +/- 1.132 (3 samples) ms",
                new DimensionalOnlineMeasure(12.3E7, 12.4E7, 12.2E7)
                        .toString(AverageTimeUnit.MILLISECONDS)
            );
    }

    @Test
    public void shouldUseSecToPrint() {
        assertEquals("0.123 +/- 0.001 (3 samples) s",
                new DimensionalOnlineMeasure(12.3E7, 12.4E7, 12.2E7)
                        .toString(AverageTimeUnit.SECONDS)
            );
    }
}
