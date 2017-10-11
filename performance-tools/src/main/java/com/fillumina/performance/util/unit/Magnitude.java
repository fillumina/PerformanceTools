package com.fillumina.performance.util.unit;

/**
 *
 * @see https://en.wikipedia.org/wiki/Metric_prefix
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public enum Magnitude implements Unit<Magnitude> {
    YOCTO(1E-24, "y"),
    ZEPTO(1E-21, "z"),
    ATTO(1E-18, "a"),
    FEMTO(1E-15, "f"),
    PICO(1E-12, "p"),
    NANO(1E-9, "n"),
    MICRO(1E-6, "u"),
    MILLI(1E-3, "m"),
    UNIT(1.0, ""),
    KILO(1E3, "K"),
    MEGA(1E6, "M"),
    GIGA(1E9, "G"),
    TERA(1E12, "T"),
    PETA(1E15, "P"),
    EXA(1E18, "E"),
    ZETTA(1E21, "Z"),
    YOTTA(1E24, "Y");

    public static final Units<Magnitude> UNITS = new Units<>(values());

    @Override
    public Units<Magnitude> units() {
        return UNITS;
    }

    private final double factor;
    private final String symbol;

    @Override
    public double getFactor() {
        return factor;
    }

    private Magnitude(double factor, String symbol) {
        this.factor = factor;
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return symbol;
    }
}
