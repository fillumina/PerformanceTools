package com.fillumina.performance.util.unit;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public enum FrequencyUnit implements Unit {
    UNIT(1.0, "Hz"),
    KILO(1E3, "KHz"),
    MEGA(1E6, "MHz"),
    GIGA(1E9, "GHz"),
    TERA(1E12, "THz"),
    PETA(1E15, "PHz"),
    EXA(1E18, "EHz"),
    ZETTA(1E21, "ZHz"),
    YOTTA(1E24, "YHz");

    private static final UnitHelper<?> HELPER = new UnitHelper<>(values());

    private final double factor;
    private final String symbol;

    @Override
    public double getFactor() {
        return factor;
    }

    public static UnitHelper<?> getHelper() {
        return HELPER;
    }

    private FrequencyUnit(double factor, String symbol) {
        this.factor = factor;
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return symbol;
    }
}
