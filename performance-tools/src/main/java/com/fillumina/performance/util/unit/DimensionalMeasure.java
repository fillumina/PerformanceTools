package com.fillumina.performance.util.unit;

import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.Ratio;
import java.util.Locale;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface DimensionalMeasure extends Measure {

    Unit getUnit();

    default String toString(Unit unit) {
        return toStringForConfidence(Ratio.P_95, unit);
    }

    default String toStringForConfidenceWitoutSamples(Ratio confidence, Unit unit) {
        double mean = unit.convertFromBase(getMean());
        double moe = unit.convertFromBase(getMarginOfError(confidence));
        return String.format(Locale.US, "%.3f +/- %.3f %s",
            mean, moe, unit);
    }

    default String toStringForConfidence(Ratio confidence, Unit unit) {
        double mean = unit.convertFromBase(getMean());
        double moe = unit.convertFromBase(getMarginOfError(confidence));
        return String.format(Locale.US, "%.3f +/- %.3f (%d samples) %s",
            mean, moe, getCount(), unit);
    }
}
