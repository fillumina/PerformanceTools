package com.fillumina.performance.util.unit;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class UnitTest {

    private enum UnitImpl implements Unit<UnitImpl> {
        MILLI(1E-3, "m"),
        UNIT(1.0, ""),
        KILO(1E3, "k");

        public static final Units<UnitImpl> UNITS = new Units<>(values());

        private final double factor;
        private final String symbol;

        @Override
        public double getFactor() {
            return factor;
        }

        @Override
        public Units<UnitImpl> units() {
            return UNITS;
        }

        private UnitImpl(double factor, String symbol) {
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
        assertEquals(1E3, UnitImpl.UNIT.convert(1, UnitImpl.KILO), 0);
    }

    @Test
    public void shouldConvertToMilli() {
        assertEquals(1E6, UnitImpl.MILLI.convert(1, UnitImpl.KILO), 0);
    }

    @Test
    public void shouldConvertToUnitFromMilli() {
        assertEquals(1, UnitImpl.UNIT.convert(1E3, UnitImpl.MILLI), 0);
    }

}
