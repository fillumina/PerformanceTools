package com.fillumina.performance.util.unit;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public enum TimeUnit implements Unit {
    NANOSECONDS(1.0, "ns"),
    MICROSECONDS(1_000.0, "us"),
    MILLISECONDS(1_000_000.0, "ms"),
    SECONDS(1_000_000_000.0, "s"),
    MINUTES(1_000_000_000.0 * 60.0, "m"),
    HOURS(1_000_000_000.0 * 60.0 * 24.0, "h");

    private static final UnitFormatter<TimeUnit> FORMATTER =
            new UnitFormatter<>(NANOSECONDS);
    private final double factor;
    private final String symbol;

    private TimeUnit(double factor, String symbol) {
        this.factor = factor;
        this.symbol = symbol;
    }

    @Override
    public double convert(final double value) {
        return value / factor;
    }

    @Override
    public String toString() {
        return symbol;
    }

    @Override
    public TimeUnit[] allValues() {
        return values();
    }

    @Override
    public UnitFormatter<TimeUnit> getFormatter() {
        return FORMATTER;
    }
}
