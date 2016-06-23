package com.fillumina.performance.util.unit;

import com.fillumina.performance.util.stats.OnlineMeasure;
import java.util.Collection;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ConcreteOnlineMeasure extends OnlineMeasure
        implements ConcreteMeasure {
    private static final long serialVersionUID = 1L;
    private final Unit unit;

    public ConcreteOnlineMeasure() {
        this(AbsoluteUnit.INSTANCE);
    }

    public ConcreteOnlineMeasure(Unit unit) {
        this.unit = unit;
    }

    public ConcreteOnlineMeasure(Unit unit, double... values) {
        super(values);
        this.unit = unit;
    }

    public ConcreteOnlineMeasure(Unit unit,
            Collection<? extends Number> collection) {
        super(collection);
        this.unit = unit;
    }

    public ConcreteOnlineMeasure(Unit unit, OnlineMeasure other) {
        super(other);
        this.unit = unit;
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
}
