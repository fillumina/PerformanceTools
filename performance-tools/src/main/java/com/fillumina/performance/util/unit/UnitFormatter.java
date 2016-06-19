package com.fillumina.performance.util.unit;

import com.fillumina.performance.util.stats.Measure;
import java.io.Serializable;

/**
 *
 * @author Francesco Illuminati
 */
public class UnitFormatter<U extends Unit> implements Serializable {
    private static final long serialVersionUID = 1L;

    private final U base;

    public UnitFormatter(U base) {
        this.base = base;
    }

    public double convert(final double value) {
        final long hundredNano = Math.round(value * 100);
        return base.convert(hundredNano) / 100d;
    }

    /** @return the right {@link TimeUnit} depending on the given magnitude
     *           of nanoseconds.
     */
    @SuppressWarnings("unchecked")
    public U getUnit(final double value) {
        final Unit[] allValues = base.allValues();
        final int l = allValues.length - 1;
        Unit u, v = allValues[0];
        double a, b = v.convert(value);
        for (int i=0; i<l; i++) {
            a = b;
            u = v;
            v = allValues[i + 1];
            b = v.convert(value);
            if (a < 999.0 && b < 1) {
                return (U) u;
            }
        }
        return (U) v;
    }

    public String prettyPrint(final Measure m) {
        final double mean = m.getMean();
        final U unit = getUnit(mean);
        final double cMean = unit.convert(mean);
        final double cMargin = unit.convert(m.getMarginOfError(0.99));
        return String.format("%.4f ± %.4f ", cMean, cMargin)  + unit.toString();
    }

    /** @param value time in nanoseconds. */
    public String prettyPrint(final long value) {
        final U result = getUnit(value);
        return print(value, result);
    }

    public String print(final double value, final U unit) {
        return String.format("%.4f ", convert(value)) + unit.toString();
    }

    public String formatUnit(final double value, final U unit) {
        return formatUnit("%,10.2f ", value, unit);
    }

    public String formatUnit(final String format,
            final double value, final U unit) {
        return String.format(format, unit.convert(value)) + " " + unit;
    }

    public U getUnit(final double[] values) {
        return getUnit(min(values));
    }

    private static double min(final double[] values) {
        double min = Double.POSITIVE_INFINITY;
        for (double v : values) {
            if (v < min) {
                min = v;
            }
        }
        return min;
    }
}
