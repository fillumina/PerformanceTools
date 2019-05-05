package com.fillumina.performance.util.unit;

import com.fillumina.performance.util.stats.ImmutableMeasure;
import com.fillumina.performance.util.stats.MarginOfErrorConfidenceInterval;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.Ratio;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ImmutableDimensionalMeasure extends DimensionalMeasure {
    private final Measure measure;
    private final Unit<?> unit;

    public ImmutableDimensionalMeasure(DimensionalMeasure measure) {
        this.measure = new ImmutableMeasure(measure);
        this.unit = measure.getUnit();
    }

    public ImmutableDimensionalMeasure(Measure measure, Unit<?> unit) {
        this.measure = new ImmutableMeasure(measure);
        this.unit = unit;
    }

    @Override
    public long getCount() {
        return measure.getCount();
    }

    @Override
    public double getMax() {
        return measure.getMax();
    }

    @Override
    public double getMean() {
        return measure.getMean();
    }

    @Override
    public double getMin() {
        return measure.getMin();
    }

    @Override
    public double getSum() {
        return measure.getSum();
    }

    @Override
    public double getUnbiasedVariance() {
        return measure.getUnbiasedVariance();
    }

    @Override
    public double getVariance() {
        return measure.getVariance();
    }

    @Override
    public Ratio getFractionalUncertainty(Ratio confidence) {
        return measure.getFractionalUncertainty(confidence);
    }

    @Override
    public double getStandardDeviation() {
        return measure.getStandardDeviation();
    }

    @Override
    public double getStandardError() {
        return measure.getStandardError();
    }

    @Override
    public double getUnbiasedStandardDeviation() {
        return measure.getUnbiasedStandardDeviation();
    }

    @Override
    public double getMarginOfError(Ratio confidence) {
        return measure.getMarginOfError(confidence);
    }

    @Override
    public MarginOfErrorConfidenceInterval getConfidenceInterval(
            Ratio confidence) {
        return measure.getConfidenceInterval(confidence);
    }

    @Override
    public String toStringForConfidence(Ratio confidence) {
        return measure.toStringForConfidence(confidence) + " " + unit;
    }

    @Override
    public Unit<?> getUnit() {
        return unit;
    }

    @Override
    public String toString() {
        return measure.toString() + " " + unit;
    }
}
