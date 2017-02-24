package com.fillumina.performance.util.unit;

import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.OnlineMeasure;
import java.util.Collection;
import java.util.Locale;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class DimensionalOnlineMeasure extends OnlineMeasure
        implements DimensionalMeasure {
    private static final long serialVersionUID = 1L;
    private final Unit unit;

    public DimensionalOnlineMeasure() {
        this(AbsoluteUnit.INSTANCE);
    }

    public DimensionalOnlineMeasure(double... values) {
        super(values);
        this.unit = AbsoluteUnit.INSTANCE;
    }

    public DimensionalOnlineMeasure(Collection<? extends Number> collection) {
        super(collection);
        this.unit = AbsoluteUnit.INSTANCE;
    }

    public DimensionalOnlineMeasure(Measure other) {
        super(other);
        this.unit = AbsoluteUnit.INSTANCE;
    }

    public DimensionalOnlineMeasure(Unit unit) {
        this.unit = unit;
    }

    public DimensionalOnlineMeasure(Unit unit, double... values) {
        super(values);
        this.unit = unit;
    }

    public DimensionalOnlineMeasure(Unit unit,
            Collection<? extends Number> collection) {
        super(collection);
        this.unit = unit;
    }

    public DimensionalOnlineMeasure(Unit unit, Measure other) {
        super(other);
        this.unit = unit;
    }

    public DimensionalOnlineMeasure(DimensionalMeasure other) {
        super(other);
        this.unit = other.getUnit();
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
    public String toStringForConfidence(double confidence) {
        return super.toStringForConfidence(confidence) + " " + unit;
    }

    @Override
    public String toString(Unit unit) {
        return toStringForConfidence(0.95, unit);
    }

    @Override
    public String toStringForConfidence(double confidence, Unit unit) {
        double mean = unit.convertFromBase(getMean());
        double moe = unit.convertFromBase(getMarginOfError(confidence));
        return String.format(Locale.US, "%.6f ± %.6f (%d samples) %s",
            mean, moe, getCount(), unit);
    }
}
