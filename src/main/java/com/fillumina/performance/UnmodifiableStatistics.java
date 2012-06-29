package com.fillumina.performance;

import java.io.Serializable;

/**
 * This is an unmodifiable live-view of a {@link Statistics} object.
 *
 * @author fra
 */
public class UnmodifiableStatistics implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Statistics stats;

    public UnmodifiableStatistics(final Statistics stats) {
        this.stats = stats;
    }

    public double variance() {
        return stats.variance();
    }

    public double sum() {
        return stats.sum();
    }

    public double standardDeviation() {
        return stats.standardDeviation();
    }

    public double min() {
        return stats.min();
    }

    public double max() {
        return stats.max();
    }

    public long count() {
        return stats.count();
    }

    public double average() {
        return stats.average();
    }

}
