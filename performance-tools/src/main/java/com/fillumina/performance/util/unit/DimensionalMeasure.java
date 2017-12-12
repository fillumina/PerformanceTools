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

    public String toStringForConfidenceWitoutSamples(Ratio confidence,
            Unit<?> unit) {
        double mean = unit.convert(getMean(), getUnit());
        double moe = unit.convert(getMarginOfError(confidence), getUnit());
        return String.format(Locale.US, "%.3f +/- %.3f %s",
            mean, moe, unit);
    }

    public String toStringForConfidence(Ratio confidence, Unit<?> unit) {
        double mean = unit.convert(getMean(), getUnit());
        double moe = unit.convert(getMarginOfError(confidence), getUnit());
        return String.format(Locale.US, "%.3f +/- %.3f (%d samples) %s",
            mean, moe, getCount(), unit);
    }
}
