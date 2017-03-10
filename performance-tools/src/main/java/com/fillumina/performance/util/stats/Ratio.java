package com.fillumina.performance.util.stats;

import java.util.Locale;

/**
 * A ratio can be expressed as a decimal (0.23) or as a percentage (23 %).
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class Ratio {
    private final double decimal;
    private static final double PRECISION = 1E6;

    public static final Ratio ZERO = Ratio.decimal(0);
    public static final Ratio P_95 = Ratio.percentage(95);
    public static final Ratio P_99 = Ratio.percentage(99);
    public static final Ratio P_999 = Ratio.percentage(99.9);
    public static final Ratio P_100 = Ratio.percentage(100);

    /** Set the ratio as a decimal. i.e. 2% is entered here as 0.02 */
    public static Ratio decimal(double decimal) {
        return new Ratio(decimal);
    }

    /** Set the ratio as a percentage. i.e. 0.02 is entered here as 2 */
    public static Ratio percentage(double percentage) {
        return new Ratio(percentage / 100.0);
    }

    protected Ratio(double decimal) {
        if (decimal < 0.0) {
            throw new IllegalArgumentException("ratio cannot be negative");
        }
        this.decimal = decimal;
    }

    /**
     * @return the ratio as a percentage = decimal * 100.0
     * (<b>rounded to the 6th decimal to avoid approximation errors</b>).
     */
    public double getPercentage() {
        double perc = decimal * 100.0;
        return Math.round(perc * PRECISION) / PRECISION;
    }

    /** @return the decimal as fractional */
    public double getDecimal() {
        return decimal;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 17 * hash +
                (int) (Double.doubleToLongBits(this.decimal) ^
                (Double.doubleToLongBits(this.decimal) >>> 32));
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
        return Double.doubleToLongBits(this.decimal) ==
                Double.doubleToLongBits(other.decimal);
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "%.3f %%", getPercentage());
    }
}
