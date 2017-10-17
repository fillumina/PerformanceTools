package com.fillumina.performance.util.unit;

import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.Ratio;
import java.util.Locale;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class Units<U extends Unit<U>> {

    private final U[] values;

    public Units(U[] units) {
        this.values = units;
    }

    public U minUnit(U a, U b) {
        return indexOfUnit(a) < indexOfUnit(b) ? a : b;
    }

    public U maxUnit(U a, U b) {
        return indexOfUnit(a) > indexOfUnit(b) ? a : b;
    }

    public U calculateAppropriatedUnitFrom(final double... values) {
        return calculateAppropriatedUnit(min(values));
    }

    /**
     * @return the most closed {@link Unit} scale.
     */
    public U calculateAppropriatedUnit(double value) {
        final int l = values.length;
        U u;
        U v = values[0];
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

    public U getBase() {
        return values[0];
    }

    public int indexOfUnit(U unit) {
        for (int i=0, l=values.length; i<l; i++) {
            if (unit == values[i]) {
                return i;
            }
        }
        return -1;
    }

    public U greaterUnit(U unit) {
        int index = indexOfUnit(unit);
        if (index < values.length - 1) {
            return values[index + 1];
        }
        return null;
    }

    public U smallerUnit(U unit) {
        int index = indexOfUnit(unit);
        if (index > 0) {
            return values[index - 1];
        }
        return null;
    }

    public String toString(Measure measureInBaseUnit, Ratio confidence) {
        double mean = measureInBaseUnit.getMean();
        U unit = calculateAppropriatedUnit(mean);
        return toString(measureInBaseUnit, confidence, unit);
    }

    public String toString(double valueInBaseUnit) {
        return toString(valueInBaseUnit, 4);
    }

    public String toString(double valueInBaseUnit, int precision) {
        U unit = calculateAppropriatedUnit(valueInBaseUnit);
        double converted = unit.convertFromBase(valueInBaseUnit);
        return String.format(Locale.US, "%,." + precision + "f %s",
                converted, unit);
    }

    public String toPrettyString(double valueInBaseUnit) {
        return toPrettyString(valueInBaseUnit, 20);
    }

    public String toPrettyString(double valueInBaseUnit, int groups) {
        double value = valueInBaseUnit;
        U unit = calculateAppropriatedUnit(value);
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
            Unit<?> dimension) {
        double mean = measureInBaseUnit.getMean();
        double margin = measureInBaseUnit.getMarginOfError(confidence);
        double convertedMean = dimension.convertFromBase(mean);
        double convertedMargin = dimension.convertFromBase(margin);
        return String.format(Locale.US, "%.4f +/- %.4f %s (%d samples)",
                convertedMean, convertedMargin, dimension,
                measureInBaseUnit.getCount());
    }

    public static String toString(double valueInBaseUnit, Unit<?> unit) {
        return toString(valueInBaseUnit, 4, unit);
    }

    public static String toString(double valueInBaseUnit, int precision,
            Unit<?> unit) {
        double converted = unit.convertFromBase(valueInBaseUnit);
        return String.format(
                Locale.US, "%." + precision + "f %s", converted, unit);
    }
}
