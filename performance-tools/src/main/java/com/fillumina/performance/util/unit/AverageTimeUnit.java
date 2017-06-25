package com.fillumina.performance.util.unit;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public enum AverageTimeUnit implements Unit {
    NANOSECONDS(1.0, "ns/op"),
    MICROSECONDS(1_000.0, "us/op"),
    MILLISECONDS(1_000_000.0, "ms/op"),
    SECONDS(1_000_000_000.0, "s/op"),
    MINUTES(1_000_000_000.0 * 60.0, "m/op"),
    HOURS(1_000_000_000.0 * 60.0 * 60.0, "h/op"),
    DAYS(1_000_000_000.0 * 60.0 * 60.0 * 24.0, "d/op");

    public static final Units<AverageTimeUnit> UNITS = new Units<>(values());

    private final double factor;
    private final String symbol;

    @Override
    public Units<AverageTimeUnit> units() {
        return UNITS;
    }

    private AverageTimeUnit(double factor, String symbol) {
        this.factor = factor;
        this.symbol = symbol;
    }

    @Override
    public double getFactor() {
        return factor;
    }

    @Override
    public String toString() {
        return symbol;
    }
}
