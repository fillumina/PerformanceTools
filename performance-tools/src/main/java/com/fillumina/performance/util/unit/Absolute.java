package com.fillumina.performance.util.unit;

/**
 * Use as a bridge between {@link Quantity} and absolute values.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public enum Absolute implements Unit<Absolute> {
    UNIT;

    @Override
    public Units<Absolute> units() {
        return new Units<>(UNIT);
    }

    @Override
    public double getFactor() {
        return 1.0;
    }
}
