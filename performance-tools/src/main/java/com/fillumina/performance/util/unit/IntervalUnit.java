package com.fillumina.performance.util.unit;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
// TimeUnit is already used by java.util.concurrent
public enum IntervalUnit implements Unit {
    NANOSECONDS(1.0, "ns"),
    MICROSECONDS(1_000.0, "us"),
    MILLISECONDS(1_000_000.0, "ms"),
    SECONDS(1_000_000_000.0, "s"),
    MINUTES(1_000_000_000.0 * 60.0, "m"),
    HOURS(1_000_000_000.0 * 60.0 * 60.0, "h"),
    DAYS(1_000_000_000.0 * 60.0 * 60.0 * 24.0, "d");

    private static final UnitHelper<?> HELPER = new UnitHelper<>(values());

    private final double factor;
    private final String symbol;

    public static UnitHelper<?> getHelper() {
        return HELPER;
    }

    private IntervalUnit(double factor, String symbol) {
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
