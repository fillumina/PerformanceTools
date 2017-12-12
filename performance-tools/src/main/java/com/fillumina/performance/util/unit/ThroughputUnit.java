package com.fillumina.performance.util.unit;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public enum ThroughputUnit implements Unit<ThroughputUnit> {
    OPDAY(1.0/(24.0 * 60.0 * 60.0), "op/d"),
    OPHOUR(1.0/(60.0 * 60.0), "op/h"),
    OPMIN(1.0/60.0, "op/m"),
    OP(1.0, "op/s"),
    KILOOP(1E3, "Kop/s"),
    MEGAOP(1E6, "Mop/s"),
    GIGAOP(1E9, "Gop/s"), // or OP/ns = 1E9 OP/s
    TERAOP(1E12, "Top/s"),
    PETAOP(1E15, "Pop/s"),
    EXAOP(1E18, "Eop/s"),
    ZETTAOP(1E21, "Zop/s"),
    YOTTAOP(1E24, "Yop/s");

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
