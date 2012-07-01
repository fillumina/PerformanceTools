package com.fillumina.performance;

import java.util.Collection;

/**
 * Calculates statistics over a set of data.
 * The values are not retained and all statistics
 * are calculated on the run so its memory footprint is fixed whatever amount
 * of data is collected.
 *
 * @author fra
 */
public class RunningStatistics extends Statistics {
    private static final long serialVersionUID = 1L;

    public static RunningStatistics[] createArray(final int size) {
        final RunningStatistics[] array = new RunningStatistics[size];
        for (int i=0; i<size; i++) {
            array[i] = new RunningStatistics();
        }
        return array;
    }

    public static Statistics[] convert(final RunningStatistics[] rs) {
        final int length = rs.length;
        final Statistics[] stats = new Statistics[length];
        for (int i=0; i<length; i++) {
            stats[i] = rs[i];
        }
        return stats;
    }

    public RunningStatistics(final double... values) {
        addAll(values);
    }

    public RunningStatistics(final Collection<? extends Number> collection) {
        addAll(collection);
    }

    @Override
    public final void addAll(final double... values) {
        super.addAll(values);
    }

    @Override
    public final void addAll(final Collection<? extends Number> collection) {
        super.addAll(collection);
    }

    @Override
    public final void add(final double value) {
        super.add(value);
    }

    @Override
    public final void clear() {
        super.clear();
    }
}
