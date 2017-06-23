package com.fillumina.performance.util.unit;

import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.OnlineMeasure;
import com.fillumina.performance.util.stats.Ratio;
import java.io.Serializable;
import java.util.Collection;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class DimensionalWrapperMeasure
        implements DimensionalMeasure, Serializable {
    private static final long serialVersionUID = 1L;
    private final Unit unit;
    private final Measure measure;

    public DimensionalWrapperMeasure() {
        this(AbsoluteUnit.UNIT);
    }

    public DimensionalWrapperMeasure(double... values) {
        this.measure = new OnlineMeasure(values);
        this.unit = AbsoluteUnit.UNIT;
    }

    public DimensionalWrapperMeasure(Collection<? extends Number> collection) {
        this.measure = new OnlineMeasure(collection);
        this.unit = AbsoluteUnit.UNIT;
    }

    public DimensionalWrapperMeasure(Measure other) {
        this.measure = new OnlineMeasure(other);
        this.unit = AbsoluteUnit.UNIT;
    }

    public DimensionalWrapperMeasure(Unit unit) {
        this.measure = new OnlineMeasure();
        this.unit = unit;
    }

    public DimensionalWrapperMeasure(Unit unit, double... values) {
        this.measure = new OnlineMeasure(values);
        this.unit = unit;
    }

    public DimensionalWrapperMeasure(Unit unit,
            Collection<? extends Number> collection) {
        this.measure = new OnlineMeasure(collection);
        this.unit = unit;
    }

    public DimensionalWrapperMeasure(Unit unit, Measure other) {
        this.measure = new OnlineMeasure(other);
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
    public Unit getUnit() {
        return unit;
    }
}
