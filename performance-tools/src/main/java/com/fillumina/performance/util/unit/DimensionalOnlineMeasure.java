package com.fillumina.performance.util.unit;

import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.OnlineMeasure;
import com.fillumina.performance.util.stats.Ratio;
import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class DimensionalOnlineMeasure extends DimensionalMeasure
        implements Serializable {

    private static final long serialVersionUID = 1L;
    private final Unit<?> unit;
    private final OnlineMeasure measure;

    public DimensionalOnlineMeasure() {
        this(Magnitude.UNIT);
    }

    public DimensionalOnlineMeasure(double... values) {
        this.measure = new OnlineMeasure(values);
        this.unit = Magnitude.UNIT;
    }

    public DimensionalOnlineMeasure(Collection<? extends Number> collection) {
        this.measure = new OnlineMeasure(collection);
        this.unit = Magnitude.UNIT;
    }

    public DimensionalOnlineMeasure(Measure other) {
        this.measure = new OnlineMeasure(other);
        this.unit = Magnitude.UNIT;
    }

    public DimensionalOnlineMeasure(Unit<?> unit) {
        this.measure = new OnlineMeasure();
        this.unit = unit;
    }

    public DimensionalOnlineMeasure(Unit<?> unit, double... values) {
        this.measure = new OnlineMeasure(values);
        this.unit = unit;
    }

    public DimensionalOnlineMeasure(Unit<?> unit,
            Collection<? extends Number> collection) {
        this.measure = new OnlineMeasure(collection);
        this.unit = unit;
    }

    public DimensionalOnlineMeasure(Unit<?> unit, Measure other) {
        this.measure = new OnlineMeasure(other);
        this.unit = unit;
    }

    public DimensionalOnlineMeasure(DimensionalMeasure other) {
        this.measure = new OnlineMeasure(other);
        this.unit = other.getUnit();
    }

    public OnlineMeasure addAll(double... values) {
        return measure.addAll(values);
    }

    public OnlineMeasure addAll(
            Iterable<? extends Number> collection) {
        return measure.addAll(collection);
    }

    public OnlineMeasure addSample(double value) {
        return measure.addSample(value);
    }

    @Override
    public Unit<?> getUnit() {
        return unit;
    }

    @Override
    public double getMax() {
        return measure.getMax();
    }

    @Override
    public double getMin() {
        return measure.getMin();
    }

    @Override
    public long getCount() {
        return measure.getCount();
    }

    @Override
    public double getSum() {
        return measure.getSum();
    }

    @Override
    public double getMean() {
        return measure.getMean();
    }

    @Override
    public double getVariance() {
        return measure.getVariance();
    }

    @Override
    public double getUnbiasedVariance() {
        return measure.getUnbiasedVariance();
    }

    public void clear() {
        measure.clear();
    }

    @Override
    public String toStringForConfidence(Ratio confidence) {
        return super.toStringForConfidence(confidence) + " " + unit;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + Objects.hashCode(this.unit);
        hash = 79 * hash + Objects.hashCode(this.measure);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DimensionalOnlineMeasure other = (DimensionalOnlineMeasure) obj;
        if (!Objects.equals(this.unit, other.unit)) {
            return false;
        }
        if (!Objects.equals(this.measure, other.measure)) {
            return false;
        }
        return true;
    }

}
