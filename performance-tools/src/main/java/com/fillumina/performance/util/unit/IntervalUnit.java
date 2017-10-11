package com.fillumina.performance.util.unit;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public enum IntervalUnit implements Unit<IntervalUnit> {
    NANOSECONDS(1.0, "ns"),
    MICROSECONDS(1_000.0, "us"),
    MILLISECONDS(1_000_000.0, "ms"),
    SECONDS(1_000_000_000.0, "s"),
    MINUTES(1_000_000_000.0 * 60.0, "m"),
    HOURS(1_000_000_000.0 * 60.0 * 60.0, "h"),
    DAYS(1_000_000_000.0 * 60.0 * 60.0 * 24.0, "d");

    public static final Units<IntervalUnit> UNITS = new Units<>(values());

    private final double factor;
    private final String symbol;

    @Override
    public Units<IntervalUnit> units() {
        return UNITS;
    }

    private IntervalUnit(double factor, String symbol) {
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
