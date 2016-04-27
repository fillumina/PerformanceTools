package com.fillumina.performance.util.stats;

/**
 * Calculates live statistics over a set of data.
 * The values are not retained and all statistics
 * are calculated on the run so its memory footprint is fixed whatever amount
 * of data is collected.
 *
 * @author Francesco Illuminati
 */
public class RunningOnlineMeasure extends OnlineMeasure {
    private static final long serialVersionUID = 1L;

    /** Clone Constructor. */
    public RunningOnlineMeasure(RunningOnlineMeasure other) {
        super(other);
    }

    public RunningOnlineMeasure(final double... values) {
        addAll(values);
    }

    public RunningOnlineMeasure(final Iterable<? extends Number> collection) {
        addAll(collection);
    }

    @Override
    public RunningOnlineMeasure addAll(final double... values) {
        super.addAll(values);
        return this;
    }

    @Override
    public RunningOnlineMeasure addAll(final Iterable<? extends Number> collection) {
        super.addAll(collection);
        return this;
    }

    @Override
    public RunningOnlineMeasure add(final double value) {
        super.add(value);
        return this;
    }

    @Override
    public final void clear() {
        super.clear();
    }
}
