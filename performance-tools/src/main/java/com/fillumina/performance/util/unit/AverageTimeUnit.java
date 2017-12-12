package com.fillumina.performance.util.unit;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public enum AverageTimeUnit implements Unit<AverageTimeUnit> {
    NANOSECONDS(1E-9, "ns/op"),
    MICROSECONDS(1E-6, "us/op"),
    MILLISECONDS(1E-3, "ms/op"),
    SECONDS(1.0, "s/op"),
    MINUTES(60.0, "m/op"),
    HOURS(60.0 * 60.0, "h/op"),
    DAYS(60.0 * 60.0 * 24.0, "d/op");

    public static final Units<AverageTimeUnit> UNITS = new Units<>(values());

    private final double factor;
    private final String symbol;

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

        public Quantity<AverageTimeUnit> get() {
            return new Quantity<>(value, NANOSECONDS);
        }
    }

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
