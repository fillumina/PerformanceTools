package com.fillumina.performance.util.stats;

/**
 * Calculates live statistics over a set of data.
 * The values are not retained and all statistics
 * are calculated on the run so its memory footprint is fixed whatever amount
 * of data is collected.
 *
 * @author Francesco Illuminati
 */
public class RunningMeasure extends Measure {
    private static final long serialVersionUID = 1L;

    /** Clone Constructor. */
    public RunningMeasure(RunningMeasure other) {
        super(other);
    }

    public RunningMeasure(final double... values) {
        addAll(values);
    }

    public RunningMeasure(final Iterable<? extends Number> collection) {
        addAll(collection);
    }

    @Override
    public RunningMeasure addAll(final double... values) {
        super.addAll(values);
        return this;
    }

    @Override
    public RunningMeasure addAll(final Iterable<? extends Number> collection) {
        super.addAll(collection);
        return this;
    }

    @Override
    public RunningMeasure add(final double value) {
        super.add(value);
        return this;
    }

    @Override
    public final void clear() {
        super.clear();
    }
}
