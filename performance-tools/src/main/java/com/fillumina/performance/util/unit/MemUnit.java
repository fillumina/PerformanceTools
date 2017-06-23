package com.fillumina.performance.util.unit;

/**
 *
 * @see <a href='https://en.wikipedia.org/wiki/Byte'>Byte (wikipedia)</a>
 * @author Francesco Illuminati
 */
public enum MemUnit implements Unit {

    B(1L), KiB(1L << 10), MiB(1L << 20), GiB(1L << 30), TiB(1L << 40),
    PiB(1L << 50), EiB(1L << 60);

    private static final UnitHelper<?> HELPER = new UnitHelper<>(values());

    public static UnitHelper<?> getHelper() {
        return HELPER;
    }

    final private long factor;

    MemUnit(long factor) {
        this.factor = factor;
    }

    /** The returned value is not precise. */
    @Override
    public double getFactor() {
        return factor;
    }

    @Override
    public double convert(final double value, final Unit unit) {
        return value / unit.convertFromBase(1.0) / factor;
    }

    @Override
    public double convertFromBase(final double value) {
        return value / factor;
    }

    @Override
    public double convertToBase(final double value) {
        return value * factor;
    }
}
