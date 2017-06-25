package com.fillumina.performance.util.unit;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public enum ThroughputUnit implements Unit {
    UNIT(1.0, "op/s"),
    KILO(1E3, "Kop/s"),
    MEGA(1E6, "Mop/s"),
    GIGA(1E9, "Gop/s"),
    TERA(1E12, "Top/s"),
    PETA(1E15, "Pop/s"),
    EXA(1E18, "Eop/s"),
    ZETTA(1E21, "Zop/s"),
    YOTTA(1E24, "Yop/s");

    public static final Units<ThroughputUnit> UNITS = new Units<>(values());

    private final double factor;
    private final String symbol;

    @Override
    public double getFactor() {
        return factor;
    }

    @Override
    public Units<ThroughputUnit> units() {
        return UNITS;
    }

    private ThroughputUnit(double factor, String symbol) {
        this.factor = factor;
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return symbol;
    }
}
