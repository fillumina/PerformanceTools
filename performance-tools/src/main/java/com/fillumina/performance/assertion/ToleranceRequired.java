package com.fillumina.performance.assertion;

import com.fillumina.performance.util.stats.Ratio;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ToleranceRequired extends Ratio {
    private static final ToleranceRequired TOO_HIGH =
            new ToleranceRequired(Double.POSITIVE_INFINITY);
    private static final ToleranceRequired ZERO =
            new ToleranceRequired(0);

    public static ToleranceRequired tooHigh() {
        return TOO_HIGH;
    }

    static ToleranceRequired zero() {
        return ZERO;
    }

    public ToleranceRequired(double percentage) {
        super(percentage / 100.0);
    }

    public boolean isZero() {
        return getDecimal() == 0;
    }

    public boolean isTooHigh() {
        return getDecimal() == Double.POSITIVE_INFINITY;
    }

    @Override
    public String toString() {
        if (isTooHigh()) {
            return "[too high]";
        }
        return super.toString();
    }

}
