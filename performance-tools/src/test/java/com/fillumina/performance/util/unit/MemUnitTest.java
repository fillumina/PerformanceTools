package com.fillumina.performance.util.unit;

import java.util.Arrays;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MemUnitTest {

    @Test
    public void shouldConvertFromBytesToGigabytes() {
        double gb = MemUnit.GiB.convertFromBase(1L << 30);
        assertEquals(1, gb, 0);
    }

    @Test
    public void shouldConvertGigabytes() {
       double b = MemUnit.GiB.convert(1, MemUnit.TiB);
       assertEquals(1 << 10, b, 0);
    }

    @Test
    public void shouldConvertGigabytesToBytes() {
       double gb = MemUnit.B.convert(1, MemUnit.GiB);
       assertEquals(1L << 30, gb, 0);
    }

    @Test
    public void shouldReturnTheSymbols() {
        assertEquals("B", MemUnit.B.toString());
        assertEquals("KiB", MemUnit.KiB.toString());
        assertEquals("MiB", MemUnit.MiB.toString());
        assertEquals("GiB", MemUnit.GiB.toString());
        assertEquals("TiB", MemUnit.TiB.toString());
        assertEquals("PiB", MemUnit.PiB.toString());
        assertEquals("EiB", MemUnit.EiB.toString());
    }

    @Test
    public void shouldReturnTheListOfAvailableUnits() {
        assertEquals(Arrays.asList(
                    MemUnit.B,
                    MemUnit.KiB,
                    MemUnit.MiB,
                    MemUnit.GiB,
                    MemUnit.TiB,
                    MemUnit.PiB,
                    MemUnit.EiB),
                Arrays.asList(MemUnit.values()));
    }

    @Test
    public void shouldFindTheRightUnit() {
        Unit<?> dimension = MemUnit.B.bestUnit(12.23E6);
        assertEquals(MemUnit.MiB, dimension);
    }

    @Test
    public void shouldFormat() {
        assertEquals("123,456,789.0000 B", MemUnit.B.toString(0.123456789E9));
    }

    @Test
    public void shouldPrettyFormatWith1Unit() {
        assertEquals("117 MiB", MemUnit.B.toPrettyString(0.123456789E9, 1));
    }

    @Test
    public void shouldPrettyFormatWith2Units() {
        assertEquals("117 MiB 755 KiB",
                MemUnit.B.toPrettyString(0.123456789E9, 2));
    }

    @Test
    public void shouldPrettyFormatWith3Units() {
        assertEquals("117 MiB 755 KiB 277 B",
                MemUnit.B.toPrettyString(0.123456789E9, 3));
    }

    @Test
    public void shouldFormatBestString() {
        assertEquals("117.7376 MiB", MemUnit.B.toBestString(0.123456789E9, 4));
    }

    @Test
    public void shouldUseQuantityBuilder() {
        Quantity<MemUnit> q = MemUnit.quantity().T(2).G(100).get();
        // is counted in base 2
        assertEquals(2.097, q.as(MemUnit.TiB), 0.1);
        assertEquals(2048 + 100, q.as(MemUnit.GiB), 0);
    }
}
