package com.fillumina.performance.util.unit;

import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.OnlineMeasure;
import com.fillumina.performance.util.stats.Ratio;
import java.util.Collection;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class DimensionalOnlineMeasure extends OnlineMeasure
        implements DimensionalMeasure {
    private static final long serialVersionUID = 1L;
    private final Unit<?> unit;

    public DimensionalOnlineMeasure() {
        this(Magnitude.UNIT);
    }

    public DimensionalOnlineMeasure(double... values) {
        super(values);
        this.unit = Magnitude.UNIT;
    }

    public DimensionalOnlineMeasure(Collection<? extends Number> collection) {
        super(collection);
        this.unit = Magnitude.UNIT;
    }

    public DimensionalOnlineMeasure(Measure other) {
        super(other);
        this.unit = Magnitude.UNIT;
    }

    public DimensionalOnlineMeasure(Unit<?> unit) {
        this.unit = unit;
    }

    public DimensionalOnlineMeasure(Unit<?> unit, double... values) {
        super(values);
        this.unit = unit;
    }

    public DimensionalOnlineMeasure(Unit<?> unit,
            Collection<? extends Number> collection) {
        super(collection);
        this.unit = unit;
    }

    public DimensionalOnlineMeasure(Unit<?> unit, Measure other) {
        super(other);
        this.unit = unit;
    }

    public DimensionalOnlineMeasure(DimensionalMeasure other) {
        super(other);
        this.unit = other.getUnit();
    }

    @Override
    public Unit<?> getUnit() {
        return unit;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public String toStringForConfidence(Ratio confidence) {
        return super.toStringForConfidence(confidence) + " " + unit;
    }
}
