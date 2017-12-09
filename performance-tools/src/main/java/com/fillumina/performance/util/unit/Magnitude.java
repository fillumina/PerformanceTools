package com.fillumina.performance.util.unit;

/**
 *
 * @see https://en.wikipedia.org/wiki/Metric_prefix
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public enum Magnitude implements Unit<Magnitude> {
    YOCTO(1E-24, "y"),
    ZEPTO(1E-21, "z"),
    ATTO(1E-18, "a"),
    FEMTO(1E-15, "f"),
    PICO(1E-12, "p"),
    NANO(1E-9, "n"),
    MICRO(1E-6, "u"),
    MILLI(1E-3, "m"),
    UNIT(1.0, ""),
    KILO(1E3, "K"),
    MEGA(1E6, "M"),
    GIGA(1E9, "G"),
    TERA(1E12, "T"),
    PETA(1E15, "P"),
    EXA(1E18, "E"),
    ZETTA(1E21, "Z"),
    YOTTA(1E24, "Y");

    public static final Units<Magnitude> UNITS = new Units<>(values());

    public static Builder quantity() {
        return new Builder();
    }

    public static class Builder {
        private double value;

        public Builder y(double v) {
            value += v * YOCTO.getFactor();
            return this;
        }

        public Builder z(double v) {
            value += v * ZEPTO.getFactor();
            return this;
        }

        public Builder a(double v) {
            value += v * ATTO.getFactor();
            return this;
        }

        public Builder f(double v) {
            value += v * FEMTO.getFactor();
            return this;
        }

        public Builder p(double v) {
            value += v * PICO.getFactor();
            return this;
        }

        public Builder n(double v) {
            value += v * NANO.getFactor();
            return this;
        }

        public Builder mc(double v) {
            value += v * MICRO.getFactor();
            return this;
        }

        public Builder m(double v) {
            value += v * MILLI.getFactor();
            return this;
        }

        public Builder u(double v) {
            value += v * UNIT.getFactor();
            return this;
        }

        public Builder K(double v) {
            value += v * KILO.getFactor();
            return this;
        }

        public Builder M(double v) {
            value += v * MEGA.getFactor();
            return this;
        }

        public Builder G(double v) {
            value += v * GIGA.getFactor();
            return this;
        }

        public Builder T(double v) {
            value += v * TERA.getFactor();
            return this;
        }

        public Builder P(double v) {
            value += v * PETA.getFactor();
            return this;
        }

        public Builder E(double v) {
            value += v * EXA.getFactor();
            return this;
        }

        public Builder Z(double v) {
            value += v * ZETTA.getFactor();
            return this;
        }

        public Builder Y(double v) {
            value += v * YOTTA.getFactor();
            return this;
        }

        public Quantity<Magnitude> get() {
            return new Quantity<>(value, UNIT);
        }
    }

    @Override
    public Units<Magnitude> units() {
        return UNITS;
    }

    private final double factor;
    private final String symbol;

    @Override
    public double getFactor() {
        return factor;
    }

    private Magnitude(double factor, String symbol) {
        this.factor = factor;
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return symbol;
    }
}
