package com.fillumina.performance.util.stats;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class FakeMeasure implements Measure {

    protected long count;
    protected double variance, max, mean, min, standardDeviation, standardError;
    protected double sum, unbiasedStandardDeviation, unbiasedVariance;
    protected double marginOfError, marginOfErrorConfidenceInterval;

    @Override
    public long count() {
        return count;
    }

    @Override
    public double marginOfError(double confidence) {
        return marginOfError;
    }

    @Override
    public MarginOfErrorConfidenceInterval getConfidenceInterval(
            double confidence) {
        return new MarginOfErrorConfidenceInterval(mean,
                marginOfError, confidence);
    }

    @Override
    public double max() {
        return max;
    }

    @Override
    public double mean() {
        return mean;
    }

    @Override
    public double min() {
        return min;
    }

    @Override
    public double standardDeviation() {
        return standardDeviation;
    }

    @Override
    public double standardError() {
        return standardError;
    }

    @Override
    public double sum() {
        return sum;
    }

    @Override
    public double unbiasedStandardDeviation() {
        return unbiasedStandardDeviation;
    }

    @Override
    public double unbiasedVariance() {
        return unbiasedVariance;
    }

    @Override
    public double variance() {
        return variance;
    }

}
