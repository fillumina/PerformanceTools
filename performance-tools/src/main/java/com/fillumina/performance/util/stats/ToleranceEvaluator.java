package com.fillumina.performance.util.stats;

import java.io.Serializable;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ToleranceEvaluator implements Serializable {
    private static final long serialVersionUID = 1L;

    // using an approximation for 1 to prevent rounding errors
    private static final double ONE = 0.999999;

    private final double confidenceMultiplicationFactor;

    public ToleranceEvaluator(Ratio tolerance) {
        this.confidenceMultiplicationFactor = ONE + tolerance.getDecimal();
    }

    boolean lt(double a, double b) {
        if (b == 0) {
            return a < confidenceMultiplicationFactor - ONE;
        }
        return a < b * confidenceMultiplicationFactor;
    }

    public Value value(double x) {
        return new Value(x);
    }

    public class Value {
        private final double x;

        private Value(double value) {
            this.x = value;
        }

        public boolean equals(double y) {
            return lt(x, y) && lt(y, x);
        }

        public boolean lessThan(double y) {
            return lt(x, y);
        }

        public boolean greaterThan(double y) {
            return lt(y, x);
        }

        public boolean between(double y, double z) {
            if (y == z) {
                return x == y || equals(y);
            }
            return lt(y, z) && lt(y, x) && lt(x, z);
        }

        @Override
        public String toString() {
            return "value=" + x;
        }
    }
}
