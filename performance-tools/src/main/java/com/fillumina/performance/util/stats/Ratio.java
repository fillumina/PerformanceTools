package com.fillumina.performance.util.stats;

import java.util.Locale;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class Ratio {
    private final double ratio;

    public static final Ratio ZERO = Ratio.value(0);
    public static final Ratio P_95 = Ratio.percentage(95);
    public static final Ratio P_99 = Ratio.percentage(99);
    public static final Ratio P_100 = Ratio.percentage(100);

    /** Set the ratio as a fractional value. i.e. 2% is entered here as 0.02 */
    public static Ratio value(double fraction) {
        return new Ratio(fraction);
    }

    /** Set the ratio as a percentage. i.e. 0.02 is entered here as 2 */
    public static Ratio percentage(double percentage) {
        return new Ratio(percentage / 100.0);
    }

    private Ratio(double fraction) {
        if (fraction < 0.0) {
            throw new IllegalArgumentException("ratio cannot be negative");
        }
        this.ratio = fraction;
    }

    /** @return the ratio as a percentage = value * 100.0 */
    public double getPercentage() {
        return ratio * 100.0;
    }

    /** @return the value as fractional */
    public double getValue() {
        return ratio;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 17 * hash +
                (int) (Double.doubleToLongBits(this.ratio) ^
                (Double.doubleToLongBits(this.ratio) >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Ratio other = (Ratio) obj;
        return Double.doubleToLongBits(this.ratio) ==
                Double.doubleToLongBits(other.ratio);
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "%.3f %%", getPercentage());
    }
}
