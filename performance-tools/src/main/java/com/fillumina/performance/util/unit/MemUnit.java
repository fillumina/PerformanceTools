package com.fillumina.performance.util.unit;

/**
 *
 * @see <a href='https://en.wikipedia.org/wiki/Byte'>Byte (wikipedia)</a>
 * @author Francesco Illuminati
 */
public enum MemUnit implements Unit<MemUnit> {

    B(1L),
    KiB(1L << 10),
    MiB(1L << 20),
    GiB(1L << 30),
    TiB(1L << 40),
    PiB(1L << 50),
    EiB(1L << 60);

    public static final Units<MemUnit> UNITS = new Units<>(values());

    public static Builder quantity() {
        return new Builder();
    }

    public static class Builder {
        private double value;

        public Builder B(double v) {
            value += v * B.getFactor();
            return this;
        }

        public Builder K(double v) {
            value += v * KiB.getFactor();
            return this;
        }

        public Builder M(double v) {
            value += v * MiB.getFactor();
            return this;
        }

        public Builder G(double v) {
            value += v * GiB.getFactor();
            return this;
        }

        public Builder T(double v) {
            value += v * TiB.getFactor();
            return this;
        }

        public Builder P(double v) {
            value += v * PiB.getFactor();
            return this;
        }

        public Builder E(double v) {
            value += v * EiB.getFactor();
            return this;
        }

        public Quantity<MemUnit> get() {
            return new Quantity<>(value, B);
        }
    }

    @Override
    public Units<MemUnit> units() {
        return UNITS;
    }

    final private long factor;

    MemUnit(long factor) {
        this.factor = factor;
    }

    /** The returned value is not precise. */
    @Override
    public double getFactor() {
        return factor;
    }
}
