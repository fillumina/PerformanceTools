package com.fillumina.performance.util.unit;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public enum IntervalUnit implements Unit {
    NANOSECONDS(1.0, "ns"),
    MICROSECONDS(1_000.0, "us"),
    MILLISECONDS(1_000_000.0, "ms"),
    SECONDS(1_000_000_000.0, "s"),
    MINUTES(1_000_000_000.0 * 60.0, "m"),
    HOURS(1_000_000_000.0 * 60.0 * 60.0, "h"),
    DAYS(1_000_000_000.0 * 60.0 * 60.0 * 24.0, "d");

    public static final IntervalUnit INSTANCE = NANOSECONDS;
    public static final UnitFormatter<IntervalUnit> FORMATTER =
            new UnitFormatter<>(IntervalUnit.NANOSECONDS);
    public static final List<Unit> LIST =
            Collections.unmodifiableList(Arrays.asList((Unit[])values()));

    private final double factor;
    private final String symbol;

    private IntervalUnit(double factor, String symbol) {
        this.factor = factor;
        this.symbol = symbol;
    }

    @Override
    public double convert(final double value, final Unit unit) {
        return value / unit.convertFromBase(1.0) / factor;
    }

    @Override
    public double convertFromBase(final double value) {
        return value / factor;
    }

    @Override
    public String toString() {
        return symbol;
    }

    @Override
    public List<Unit> allAvailableUnitOfMeasures() {
        return LIST;
    }

    @Override
    public Unit getBase() {
        return NANOSECONDS;
    }

    @Override
    public UnitFormatter<IntervalUnit> getFormatter() {
        return FORMATTER;
    }
}
