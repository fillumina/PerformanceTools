package com.fillumina.performance.util.unit;

import java.util.Arrays;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati
 */
public class MemUnitFormatterTest {
    private static final double KILOBYTE = 1024;
    private static final double MEGABYTE = KILOBYTE * KILOBYTE;
    private static final double GIGABYTE = 1024 * MEGABYTE;

    @Test
    public void shouldSelectBytes() {
        assertMemUnit(MemUnit.B, 2, 3, 1, 8);
        assertMemUnit(MemUnit.B, 2, 0, 1, 100);
        assertMemUnit(MemUnit.B, 2, 0, 1, 1_000);
    }

    @Test
    public void shouldSelectKilobytes() {
        assertMemUnit(MemUnit.KiB, KILOBYTE, 1230, 2000);
        assertMemUnit(MemUnit.KiB, KILOBYTE, 2048, 89_000);
        assertMemUnit(MemUnit.KiB, KILOBYTE, 2048, 1_000 * 1_000);
    }

    @Test
    public void shouldSelectMegabytes() {
        assertMemUnit(MemUnit.MiB, MEGABYTE, 1.3 * MEGABYTE, 10 * MEGABYTE);
        assertMemUnit(MemUnit.MiB, MEGABYTE, 1.3 * MEGABYTE, 1_000 * 1_000_000);
    }

    @Test
    public void shouldSelectGigabytes() {
        assertMemUnit(MemUnit.GiB, GIGABYTE, 1.3 * GIGABYTE, 10 * GIGABYTE);
    }

    private void assertMemUnit(final MemUnit expected, double... values) {
        Unit result = MemUnit.UNITS.calculateAppropriatedUnitFrom(values);
        assertEquals(" values: " + Arrays.toString(values),
                expected, result);
    }
}
