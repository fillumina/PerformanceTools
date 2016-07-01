package com.fillumina.performance.util.unit;

import com.fillumina.performance.util.stats.Measure;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Francesco Illuminati
 */
public class UnitFormatter<U extends Unit> implements Serializable {
    private static final long serialVersionUID = 1L;

    private final U base;

    @SuppressWarnings("unchecked")
    public UnitFormatter(U baseUnit) {
        this.base = (U) baseUnit.getBase();
    }

    public String toString(Measure measureInBaseUnit, double confidence) {
        double mean = measureInBaseUnit.getMean();
        U unit = getUnit(mean);
        return toString(measureInBaseUnit, confidence, unit);
    }

    public String toString(Measure measureInBaseUnit, double confidence,
            U unit) {
        double mean = measureInBaseUnit.getMean();
        double margin = measureInBaseUnit.getMarginOfError(confidence);
        double convertedMean = unit.convert(mean, base);
        double convertedMargin = unit.convert(margin, base);
        return String.format("%.4f ± %.4f %s (%d samples)",
                convertedMean, convertedMargin, unit,
                measureInBaseUnit.getCount());
    }

    public String toString(double valueInBaseUnit) {
        U unit = getUnit(valueInBaseUnit);
        return toString(valueInBaseUnit, unit);
    }

    public String toString(double valueInBaseUnit, U unit) {
        double converted = unit.convert(valueInBaseUnit, base);
        return String.format("%.4f %s", converted, unit);
    }

    public U getMinUnit(final double[] values) {
        return getUnit(min(values));
    }

    /**
     * @return the most closed  {@link Unit}.
     */
    @SuppressWarnings("unchecked")
    public U getUnit(double value) {
        final List<Unit> allValues = base.allAvailableUnitOfMeasures();
        final int l = allValues.size();
        Unit u, v = allValues.get(0);
        double c;
        for (int i=1; i<l; i++) {
            u = v;
            v = allValues.get(i);
            c = v.convertFromBase(value);
            if (c < 1) {
                return (U) u;
            }
        }
        return (U) v;
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
