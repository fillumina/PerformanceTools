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

    private static final UnitHelper<?> HELPER = new UnitHelper<>(values());

    private final double factor;
    private final String symbol;

    public static UnitHelper<?> getHelper() {
        return HELPER;
    }

    private AbsoluteUnit(double factor, String symbol) {
        this.factor = factor;
        this.symbol = symbol;
    }

    @Override
    public double convert(double value, Unit dimension) {
        return value / dimension.convertFromBase(1.0) / factor;
    }

    @Override
    public double convertFromBase(final double value) {
        return value / factor;
    }

    @Override
    public double convertToBase(final double value) {
        return value * factor;
    }

    @Override
    public String toString() {
        return symbol;
    }
}
