package com.fillumina.performance.util.unit;

/**
 *
 * @see <a href='https://en.wikipedia.org/wiki/Byte'>Byte (wikipedia)</a>
 * @author Francesco Illuminati
 */
public enum MemUnit implements Unit<MemUnit> {

    B(1L), KiB(1L << 10), MiB(1L << 20), GiB(1L << 30), TiB(1L << 40),
    PiB(1L << 50), EiB(1L << 60);

    public static final Units<MemUnit> UNITS = new Units<>(values());

    @Override
    public Units<MemUnit> units() {
        return UNITS;
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
}
