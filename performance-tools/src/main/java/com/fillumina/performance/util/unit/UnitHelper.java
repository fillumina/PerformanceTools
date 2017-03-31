package com.fillumina.performance.util.unit;

import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.Ratio;
import java.util.Locale;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class UnitHelper<T extends Unit> {

    private final T[] values;

    public UnitHelper(T[] units) {
        this.values = units;
    }

    public Unit getUnit(final double... values) {
        return UnitHelper.this.getUnit(min(values));
    }

    /**
     * @return the most closed {@link Unit} scale.
     */
    public Unit getUnit(double value) {
        final int l = values.length;
        Unit u;
        Unit v = values[0];
        double c;
        for (int i=1; i<l; i++) {
            u = v;
            v = values[i];
            c = v.convertFromBase(value);
            if (c < 1) {
                return u;
            }
        }
        return v;
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

    public Unit greaterUnit(Unit unit) {
        for (int i=0, l=values.length; i<l; i++) {
            if (unit == values[i] && i < l - 1) {
                return values[i + 1];
            }
        }
        return null;
    }

    public Unit smallerUnit(Unit unit) {
        for (int i=0, l=values.length; i<l; i++) {
            if (unit == values[i] && i > 0) {
                return values[i - 1];
            }
        }
        return null;
    }

    public String toString(Measure measureInBaseUnit, Ratio confidence) {
        double mean = measureInBaseUnit.getMean();
        Unit dimension = getUnit(mean);
        return toString(measureInBaseUnit, confidence, dimension);
    }

    public String toString(double valueInBaseUnit) {
        return toString(valueInBaseUnit, 4);
    }

    public String toString(double valueInBaseUnit, int precision) {
        Unit unit = getUnit(valueInBaseUnit);
        double converted = unit.convertFromBase(valueInBaseUnit);
        return String.format(Locale.US, "%,." + precision + "f %s",
                converted, unit);
    }

    public String toPrettyString(double valueInBaseUnit) {
        return toPrettyString(valueInBaseUnit, 20);
    }

    public String toPrettyString(double valueInBaseUnit, int groups) {
        double value = valueInBaseUnit;
        Unit unit = getUnit(value);
        StringBuilder buf = new StringBuilder();
        for (int i=groups; i>0; i--) {
            double converted = unit.convertFromBase(value);
            double remain = Math.floor(converted);
            if (remain > 0) {
                if (buf.length() > 0) {
                    buf.append(" ");
                }
                buf.append(String.format(Locale.US, "%,.0f %s", remain, unit));
            }
            value -= unit.convertToBase(remain);
            unit = smallerUnit(unit);
            if (unit == null) {
                break;
            }
        }
        return buf.toString();
    }

    public static String toString(Measure measureInBaseUnit,
            Ratio confidence,
            Unit dimension) {
        double mean = measureInBaseUnit.getMean();
        double margin = measureInBaseUnit.getMarginOfError(confidence);
        double convertedMean = dimension.convertFromBase(mean);
        double convertedMargin = dimension.convertFromBase(margin);
        return String.format(Locale.US, "%.4f +/- %.4f %s (%d samples)",
                convertedMean, convertedMargin, dimension,
                measureInBaseUnit.getCount());
    }

    public static String toString(double valueInBaseUnit, Unit unit) {
        return toString(valueInBaseUnit, 4, unit);
    }

    public static String toString(double valueInBaseUnit, int precision,
            Unit unit) {
        double converted = unit.convertFromBase(valueInBaseUnit);
        return String.format(
                Locale.US, "%." + precision + "f %s", converted, unit);
    }
}
