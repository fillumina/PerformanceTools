package com.fillumina.performance.util.unit;

import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.Ratio;
import java.io.Serializable;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
// TODO not used...
public class DimensionalWrapperMeasure
        implements DimensionalMeasure, Serializable {
    private static final long serialVersionUID = 1L;
    private final Unit<?> unit;
    private final Measure measure;

    public DimensionalWrapperMeasure(Unit<?> unit, Measure measure) {
        this.measure = measure;
        this.unit = unit;
    }

    @Override
    public long getCount() {
        return measure.getCount();
    }

    @Override
    public double getMarginOfError(Ratio confidence) {
        return measure.getMarginOfError(confidence);
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
    public double getStandardDeviation() {
        return measure.getStandardDeviation();
    }

    @Override
    public double getStandardError() {
        return measure.getStandardError();
    }

    @Override
    public double getSum() {
        return measure.getSum();
    }

    @Override
    public double getUnbiasedStandardDeviation() {
        return measure.getUnbiasedStandardDeviation();
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
    public Unit<?> getUnit() {
        return unit;
    }
}
