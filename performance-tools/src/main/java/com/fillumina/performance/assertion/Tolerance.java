package com.fillumina.performance.assertion;

import com.fillumina.performance.util.stats.Ratio;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
// TODO substitute to Ratio in assertions?
public class Tolerance extends Ratio {
    private static final Tolerance MAXIMUM =
            new Tolerance(Double.POSITIVE_INFINITY);
    private static final Tolerance ZERO =
            new Tolerance(0);

    public static Tolerance max() {
        return MAXIMUM;
    }

    static Tolerance zero() {
        return ZERO;
    }

    protected Tolerance(double percentage) {
        super(percentage / 100.0);
    }

    public boolean isZero() {
        return getDecimal() == 0;
    }

    public boolean isMaximum() {
        return getDecimal() == Double.POSITIVE_INFINITY;
    }

    @Override
    public String toString() {
        if (isMaximum()) {
            return "[too high]";
        }
        return super.toString();
    }

}
