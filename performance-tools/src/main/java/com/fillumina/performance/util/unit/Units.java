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
    private final U base;

    /** @param units must be ordered from the lesser factor to the bigger. */
    public Units(U[] units) {
        assertUnitFactorPresent(units);
        assertAscendingFactorOrder(units);

        this.values = units;
        for (U u : units) {
            if (u.getFactor() == 1.0) {
                base = u;
                return;
            }
        }
        throw new RuntimeException("no unit with factor = 1 found as base");
    }

    private void assertAscendingFactorOrder(U[] units) {
        for (int i=1; i<units.length; i++) {
            if (units[i-1].getFactor() >= units[i].getFactor()) {
                throw new RuntimeException(
                        "wrong unit order (must be order ascending by factor)");
            }
        }
    }

    private void assertUnitFactorPresent(U[] units) {
        for (int i=0; i<units.length; i++) {
            if (units[i].getFactor() == 1.0) {
                return;
            }
        }
        throw new RuntimeException("unit factor not present");
    }

    public U minUnit(U a, U b) {
        return indexOfUnit(a) < indexOfUnit(b) ? a : b;
    }

    public U maxUnit(U a, U b) {
        return indexOfUnit(a) > indexOfUnit(b) ? a : b;
    }

    /**
     * @param values expressed in base unit (the one with factor = 1.0)
     * @return the most closed {@link Unit} scale.
     */
    public U calculateAppropriatedUnitFrom(final double... values) {
        double min = min(values);
        U minUnit = calculateAppropriatedUnit(min);
        int minIndex = indexOf(minUnit);
        double max = max(values);
        U maxUnit = calculateAppropriatedUnit(max);
        int maxIndex = indexOf(maxUnit);

        int avgUnitIndex =
                (int) Math.floor(minIndex + 1.0 * (maxIndex - minIndex) / 2.0);
        U r = getUnitAtIndex(avgUnitIndex);
        while (avgUnitIndex > 0 && r.convertFromBase(min) < 0.1) {
            avgUnitIndex--;
            r = getUnitAtIndex(avgUnitIndex);
        }

        return getUnitAtIndex(avgUnitIndex);
    }

    /**
     * @param value expressed in base unit (the one with factor = 1.0)
     * @return the most closed {@link Unit} scale.
     */
    public U calculateAppropriatedUnit(double value) {
        U u;
        U v = values[0];
        double c;
        for (int i=1,l=values.length; i<l; i++) {
            u = v;
            v = values[i];
            c = v.convertFromBase(value);
            if (c < 1) {
                return u;
            }
        }
        return v;
    }

    private U getUnitAtIndex(int index) {
        return values[index];
    }

    private int indexOf(U u) {
        for (int i=0; i<values.length; i++) {
            if (u == values[i]) {
                return i;
            }
        }
        return -1;
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

    private static double max(final double[] values) {
        double max = Double.NEGATIVE_INFINITY;
        for (double v : values) {
            if (v > max) {
                max = v;
            }
        }
        return max;
    }

    public U getBase() {
        return base;
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
