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
        Unit dimension = MemUnit.getHelper().getUnit(12.23E6);
        assertEquals(MemUnit.MiB, dimension);
    }

    @Test
    public void shouldFormatStatically() {
        assertEquals("117.7376 MiB", MemUnit.getHelper().toString(0.123456789E9));
    }

    @Test
    public void shouldFormatStaticallyByHelper() {
        assertEquals("117.7376 MiB",
                new UnitHelper<>(MemUnit.values()).toString(0.123456789E9));
    }

    @Test
    public void shouldFormatFromGivenUnit() {
        assertEquals("0.1150 GiB",
                UnitHelper.toString(0.123456789E9, 4, MemUnit.GiB));
    }
}
