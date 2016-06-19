package com.fillumina.performance.util.unit;

/**
 *
 * @author Francesco Illuminati
 */
public enum MemUnit implements Unit {

    B(1), KiB(1 << 10), MiB(1 << 20), GiB(1 << 30);

    private static final UnitFormatter<MemUnit> FORMATTER =
            new UnitFormatter<>(B);
    final private long factor;

    MemUnit(long factor) {
        this.factor = factor;
    }

    @Override
    public double convert(final double value) {

        return value / factor;
    }

    @Override
    public MemUnit[] allValues() {
        return values();
    }

    @Override
    public UnitFormatter<MemUnit> getFormatter() {
        return FORMATTER;
    }
}
