package com.fillumina.performance.util.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class UnitTest {

    private enum MyLittleUnit implements Unit<MyLittleUnit> {
        MILLI(1E-3, "m"),
        UNIT(1.0, ""),
        KILO(1E3, "k");

        public static final Units<MyLittleUnit> UNITS = new Units<>(values());

        private final double factor;
        private final String symbol;

        @Override
        public double getFactor() {
            return factor;
        }

        @Override
        public Units<MyLittleUnit> units() {
            return UNITS;
        }

        private MyLittleUnit(double factor, String symbol) {
            this.factor = factor;
            this.symbol = symbol;
        }

        @Override
        public String toString() {
            return symbol;
        }
    }

    @Test
    public void shouldConvertToUnit() {
        assertEquals(1E3, MyLittleUnit.UNIT.convert(1, MyLittleUnit.KILO), 0);
    }

    @Test
    public void shouldConvertToMilli() {
        assertEquals(1E6, MyLittleUnit.MILLI.convert(1, MyLittleUnit.KILO), 0);
    }

    @Test
    public void shouldConvertToUnitFromMilli() {
        assertEquals(1, MyLittleUnit.UNIT.convert(1E3, MyLittleUnit.MILLI), 0);
    }

    @Test
    public void shouldReturnTheUnitTypeName() {
        assertEquals("MyLittle", MyLittleUnit.MILLI.getUnitName());
    }

    @Test
    public void shouldReturnTheUnitName() {
        assertEquals("MILLI", MyLittleUnit.MILLI.name());
    }

    @Test
    public void shouldReturnTheUnitSymbol() {
        assertEquals("m", MyLittleUnit.MILLI.toString());
    }

    @Test
    public void shouldBeSameType() {
        assertTrue(MyLittleUnit.MILLI.isSameType(MyLittleUnit.KILO));
    }

    @Test
    public void shouldNotBeSameType() {
        assertFalse(MyLittleUnit.MILLI.isSameType(StrangeUnit.SHOES));
    }
}
