package com.fillumina.performance.util.stats;

import java.io.Serializable;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ImmutableMeasure extends Measure implements Serializable {
    private static final long serialVersionUID = 1L;

    private final double mean;
    private final long count;
    private final double sum;
    private final double max;
    private final double min;
    private final double variance;
    private final double unbiasedVariance;

    public ImmutableMeasure(final Measure measure) {
        this.mean = measure.getMean();
        this.count = measure.getCount();
        this.sum = measure.getSum();
        this.max = measure.getMax();
        this.min = measure.getMin();
        this.variance = measure.getVariance();
        this.unbiasedVariance = measure.getUnbiasedVariance();
    }

    @Override
    public double getMax() {
        return max;
    }

    @Override
    public double getMin() {
        return min;
    }

    @Override
    public long getCount() {
        return count;
    }

    @Override
    public double getSum() {
        return sum;
    }

    @Override
    public double getMean() {
        return mean;
    }

    @Override
    public double getVariance() {
        return variance;
    }

    @Override
    public double getUnbiasedVariance() {
        return unbiasedVariance;
    }
}
