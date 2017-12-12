package com.fillumina.performance.util.unit;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public enum IntervalUnit implements Unit<IntervalUnit> {
    NANOSECONDS(1E-9, "ns"),
    MICROSECONDS(1E-6, "us"),
    MILLISECONDS(1E-3, "ms"),
    SECONDS(1.0, "s"),
    MINUTES(60.0, "m"),
    HOURS(60.0 * 60.0, "h"),
    DAYS(60.0 * 60.0 * 24.0, "d");

    public static final Units<IntervalUnit> UNITS = new Units<>(values());

    public static Builder quantity() {
        return new Builder();
    }

    public static class Builder {
        private double value;

        public Builder ns(double v) {
            value += v * NANOSECONDS.getFactor();
            return this;
        }

        public Builder us(double v) {
            value += v * MICROSECONDS.getFactor();
            return this;
        }

        public Builder ms(double v) {
            value += v * MILLISECONDS.getFactor();
            return this;
        }

        public Builder s(double v) {
            value += v * SECONDS.getFactor();
            return this;
        }

        public Builder m(double v) {
            value += v * MINUTES.getFactor();
            return this;
        }

        public Builder h(double v) {
            value += v * HOURS.getFactor();
            return this;
        }

        public Builder d(double v) {
            value += v * DAYS.getFactor();
            return this;
        }

        public Quantity<IntervalUnit> get() {
            return new Quantity<>(value, UNITS.getBase());
        }
    }


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
