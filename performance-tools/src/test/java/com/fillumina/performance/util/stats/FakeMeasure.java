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
    public long getCount() {
        return count;
    }

    @Override
    public double getMarginOfError(double confidence) {
        return marginOfError;
    }

    @Override
    public MarginOfErrorConfidenceInterval getConfidenceInterval(
            double confidence) {
        return new MarginOfErrorConfidenceInterval(mean,
                marginOfError, confidence);
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
    public double getStandardDeviation() {
        return standardDeviation;
    }

    @Override
    public double getStandardError() {
        return standardError;
    }

    @Override
    public double getSum() {
        return sum;
    }

    @Override
    public double getUnbiasedStandardDeviation() {
        return unbiasedStandardDeviation;
    }

    @Override
    public double getUnbiasedVariance() {
        return unbiasedVariance;
    }

    @Override
    public double getVariance() {
        return variance;
    }

    @Override
    public String toStringForConfidence(double confidence) {
        return mean + " ± " + getMarginOfError(confidence) +
                " (" + count + " samples)";
    }
}
