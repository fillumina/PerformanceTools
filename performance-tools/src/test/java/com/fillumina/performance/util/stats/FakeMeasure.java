package com.fillumina.performance.util.stats;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class FakeMeasure extends AbstractMeasure {

    protected double mean, max, min, sum, unbiasedVariance, variance;
    protected long count;

    public FakeMeasure mean(final double value) {
        this.mean = value;
        return this;
    }

    public FakeMeasure max(final double value) {
        this.max = value;
        return this;
    }

    public FakeMeasure min(final double value) {
        this.min = value;
        return this;
    }

    public FakeMeasure sum(final double value) {
        this.sum = value;
        return this;
    }

    public FakeMeasure unbiasedVariance(final double value) {
        this.unbiasedVariance = value;
        return this;
    }

    public FakeMeasure variance(final double value) {
        this.variance = value;
        return this;
    }

    public FakeMeasure count(final long value) {
        this.count = value;
        return this;
    }

    @Override
    public long getCount() {
        return count;
    }

    @Override
    public double getMax() {
        return max;
    }

    @Override
    public double getMean() {
        return mean;
    }

    @Override
    public double getMin() {
        return min;
    }

    @Override
    public double getSum() {
        return sum;
    }

    @Override
    public double getUnbiasedVariance() {
        return unbiasedVariance;
    }

    @Override
    public double getVariance() {
        return variance;
    }
}
