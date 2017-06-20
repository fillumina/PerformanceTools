package com.fillumina.performance.util.unit;

import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.OnlineMeasure;
import com.fillumina.performance.util.stats.Ratio;
import java.io.Serializable;
import java.util.Collection;
import java.util.Locale;

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
        this(AbsoluteUnit.INSTANCE);
    }

    public DimensionalWrapperMeasure(double... values) {
        this.measure = new OnlineMeasure(values);
        this.unit = AbsoluteUnit.INSTANCE;
    }

    public DimensionalWrapperMeasure(Collection<? extends Number> collection) {
        this.measure = new OnlineMeasure(collection);
        this.unit = AbsoluteUnit.INSTANCE;
    }

    public DimensionalWrapperMeasure(Measure other) {
        this.measure = new OnlineMeasure(other);
        this.unit = AbsoluteUnit.INSTANCE;
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

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public String toStringForConfidence(Ratio confidence) {
        return measure.toStringForConfidence(confidence) + " " + unit;
    }

    @Override
    public String toString(Unit unit) {
        return toStringForConfidence(Ratio.P_95, unit);
    }

    @Override
    public String toStringForConfidence(Ratio confidence, Unit unit) {
        double mean = unit.convertFromBase(getMean());
        double moe = unit.convertFromBase(getMarginOfError(confidence));
        return String.format(Locale.US, "%.3f +/- %.3f (%d samples) %s",
            mean, moe, getCount(), unit);
    }
}
