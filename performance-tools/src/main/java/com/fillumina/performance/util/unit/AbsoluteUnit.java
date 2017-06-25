package com.fillumina.performance.util.unit;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public enum AbsoluteUnit implements Unit {
    UNIT(1.0, ""),
    KILO(1E3, "K"),
    MEGA(1E6, "M"),
    GIGA(1E9, "G"),
    TERA(1E12, "T"),
    PETA(1E15, "P"),
    EXA(1E18, "E"),
    ZETTA(1E21, "Z"),
    YOTTA(1E24, "Y");

    public static final Units<AbsoluteUnit> UNITS = new Units<>(values());

    @Override
    public Units<AbsoluteUnit> units() {
        return UNITS;
    }

    private final double factor;
    private final String symbol;

    @Override
    public double getFactor() {
        return factor;
    }

    private AbsoluteUnit(double factor, String symbol) {
        this.factor = factor;
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return symbol;
    }
}
