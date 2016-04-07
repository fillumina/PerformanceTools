package com.fillumina.performance.stats;

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

    public static RunningMeasure[] createArray(final int size) {
        final RunningMeasure[] array = new RunningMeasure[size];
        for (int i=0; i<size; i++) {
            array[i] = new RunningMeasure();
        }
        return array;
    }

    public static Measure[] convert(final RunningMeasure[] rs) {
        final int length = rs.length;
        final Measure[] stats = new Measure[length];
        for (int i=0; i<length; i++) {
            stats[i] = rs[i];
        }
        return stats;
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
