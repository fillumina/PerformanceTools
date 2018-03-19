package com.fillumina.performance.util.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class UnitsTest {

    private static enum WrongDuplicateUnit implements Unit<WrongDuplicateUnit> {
       UNIT(1.0, "unit"),
       WRONG_DUPLICATE_UNIT(1.0, "wrong");

       public static final Units<WrongDuplicateUnit> UNITS = new Units<>(values());

       private final double factor;
       private final String symbol;

       @Override
       public double getFactor() {
           return factor;
       }

       @Override
       public Units<WrongDuplicateUnit> units() {
           return UNITS;
       }

       private WrongDuplicateUnit(double factor, String symbol) {
           this.factor = factor;
           this.symbol = symbol;
       }

       @Override
       public String toString() {
           return symbol;
       }
   }

    @Test(expected = ExceptionInInitializerError.class)
    public void shouldNotAcceptWrongDuplicatedUnitFactor() {
        assertNotNull(WrongDuplicateUnit.UNIT);
    }

    private static enum WrongOrderUnit implements Unit<WrongOrderUnit> {
       UNIT(1.0, "unit"),
       LESSER_UNIT(1E-3, "lesser");

       public static final Units<WrongOrderUnit> UNITS = new Units<>(values());

       private final double factor;
       private final String symbol;

       @Override
       public double getFactor() {
           return factor;
       }

       @Override
       public Units<WrongOrderUnit> units() {
           return UNITS;
       }

       private WrongOrderUnit(double factor, String symbol) {
           this.factor = factor;
           this.symbol = symbol;
       }

       @Override
       public String toString() {
           return symbol;
       }
   }

    @Test(expected = ExceptionInInitializerError.class)
    public void shouldNotAcceptWrongOrderedUnit() {
        assertNotNull(WrongOrderUnit.UNIT);
    }

    private static enum ZeroFactorUnit implements Unit<ZeroFactorUnit> {
       ZERO(0.0, "zero"),
       UNIT(1.0, "unit");

       public static final Units<ZeroFactorUnit> UNITS = new Units<>(values());

       private final double factor;
       private final String symbol;

       @Override
       public double getFactor() {
           return factor;
       }

       @Override
       public Units<ZeroFactorUnit> units() {
           return UNITS;
       }

       private ZeroFactorUnit(double factor, String symbol) {
           this.factor = factor;
           this.symbol = symbol;
       }

       @Override
       public String toString() {
           return symbol;
       }
   }

    @Test(expected = ExceptionInInitializerError.class)
    public void shouldNotAcceptZeroFactorUnit() {
        assertNotNull(ZeroFactorUnit.UNIT);
    }

    private enum MyUnit implements Unit<MyUnit> {
        MILLI(1E-3, "m"),
        UNIT(1.0, ""),
        KILO(1E3, "k");

        public static final Units<MyUnit> UNITS = new Units<>(values());

        private final double factor;
        private final String symbol;

        @Override
        public double getFactor() {
            return factor;
        }

        @Override
        public Units<MyUnit> units() {
            return UNITS;
        }

        private MyUnit(double factor, String symbol) {
            this.factor = factor;
            this.symbol = symbol;
        }

        @Override
        public String toString() {
            return symbol;
        }
    }

    @Test
    public void shouldReturnTheNotNullUnitMin() {
        assertEquals(MyUnit.MILLI, Units.min(null, MyUnit.MILLI));
        assertEquals(MyUnit.MILLI, Units.min(MyUnit.MILLI, null));
    }

    @Test
    public void shouldReturnTheNotNullUnitMax() {
        assertEquals(MyUnit.MILLI, Units.max(null, MyUnit.MILLI));
        assertEquals(MyUnit.MILLI, Units.max(MyUnit.MILLI, null));
    }

    @Test
    public void shouldReturnTheMinUnit() {
        assertEquals(MyUnit.MILLI, Units.min(MyUnit.KILO, MyUnit.MILLI));
    }

    @Test
    public void shouldReturnTheSameUnitUsingMin() {
        assertEquals(MyUnit.MILLI, Units.min(MyUnit.MILLI, MyUnit.MILLI));
    }

    @Test
    public void shouldReturnTheMaxUnit() {
        assertEquals(MyUnit.KILO, Units.max(MyUnit.KILO, MyUnit.MILLI));
    }

    @Test
    public void shouldReturnTheSameUnitUsingMax() {
        assertEquals(MyUnit.MILLI, Units.max(MyUnit.MILLI, MyUnit.MILLI));
    }

    @Test
    public void shouldGetBase() {
        assertEquals(MyUnit.UNIT, MyUnit.UNIT.getBase());
    }

}
