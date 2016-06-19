package com.fillumina.performance.util.unit;

import java.util.Arrays;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati
 */
public class MemUnitFormatterTest {
    private static final double SECOND = 1E9;
    private static final double MINUTE = 60.0 * SECOND;
    private static final double HOUR = 60.0 * MINUTE;

    @Test
    public void shouldSelectNanoseconds() {
        assertmemUnit(MemUnit.B, 2, 3, 1, 8);
    }

    private void assertmemUnit(final MemUnit expected, double... values) {
        Unit result = MemUnit.B.getFormatter().getUnit(values);
        assertEquals(" values: " + Arrays.toString(values),
                expected, result);
    }
}
