package com.fillumina.performance.util.unit;

import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.Ratio;
import java.util.Locale;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class DimensionalMeasure extends Measure {

    public abstract Unit<?> getUnit();

    public String toString(Unit<?> unit) {
        return toStringForConfidence(Ratio.P_95, unit);
    }

    /**
     * @param unit the unit to be converted into.
     *          <i>the given unit must be of the same type of the actual one!</i>
     *
     * @return a new {@link DimensionalMeasure} converted to the given
     *           {@link Unit}.
     */
    public DimensionalMeasure in(Unit<?> unit) {
        if (getUnit() == unit) {
            return this;
        }
        if (!getUnit().isSameType(unit)) {
            throw new MismatchedUnitRuntimeException(getUnit(), unit);
        }
        return new DefaultDimensionalMeasure(
                multiplyBy(getUnit().getConversionFactorTo(unit)),
                unit);
    }

    public String toStringForConfidenceWitoutSamples(Ratio confidence,
            Unit<?> unit) {
        if (!getUnit().isSameType(unit)) {
            throw new RuntimeException("type mismatch: " +
                    getUnit().toString() + " vs " + unit.toString());
        }
        double mean = unit.convert(getMean(), getUnit());
        double moe = unit.convert(getMarginOfError(confidence), getUnit());
        if (Double.isNaN(moe)) {
            return String.format(Locale.US, "%.3f %s", mean, unit);
        }
        return String.format(Locale.US, "%.3f +/- %.3f %s",
            mean, moe, unit);
    }

    public String toStringForConfidence(Ratio confidence, Unit<?> unit) {
        if (!getUnit().isSameType(unit)) {
            throw new RuntimeException("type mismatch: " +
                    getUnit().toString() + " " + unit.toString());
        }
        double mean = unit.convert(getMean(), getUnit());
        double moe = unit.convert(getMarginOfError(confidence), getUnit());
        return String.format(Locale.US, "%.3f +/- %.3f (%d samples) %s",
            mean, moe, getCount(), unit);
    }
}
