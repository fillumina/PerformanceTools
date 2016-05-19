package com.fillumina.performance.stats;

import com.fillumina.performance.util.stats.ConfidenceInterval;
import com.fillumina.performance.util.stats.Measure;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class CorrectedMeasure implements Measure {
    private final Measure measure;
    private final long nanoseconds;

    public CorrectedMeasure(Measure measure, long nanoseconds) {
        this.measure = measure;
        this.nanoseconds = nanoseconds;
    }

    @Override
    public long getCount() {
        return measure.getCount();
    }

    @Override
    public ConfidenceInterval getConfidenceInterval(double confidence) {
        return measure.getConfidenceInterval(confidence);
    }

    @Override
    public double getMarginOfError(double confidence) {
        return measure.getMarginOfError(confidence);
    }

    @Override
    public double getMax() {
        return measure.getMax() - nanoseconds;
    }

    @Override
    public double getMean() {
        return measure.getMean() - nanoseconds;
    }

    @Override
    public double getMin() {
        return measure.getMin() - nanoseconds;
    }

    @Override
    public double getStandardDeviation() {
        return measure.getStandardDeviation();
    }

    @Override
    public double getStandardError() {
        return measure.getStandardError();
    }

    @Override
    public double getSum() {
        return measure.getSum();
    }

    @Override
    public double getUnbiasedStandardDeviation() {
        return measure.getUnbiasedStandardDeviation();
    }

    @Override
    public double getUnbiasedVariance() {
        return measure.getUnbiasedVariance();
    }

    @Override
    public double getVariance() {
        return measure.getVariance();
    }

    @Override
    public String toStringForConfidence(double confidence) {
        return String.format("%.4f ± %.4f (samples %d)",
            getMean(), getMarginOfError(confidence), getCount());
    }

    @Override
    public String toString() {
        return getMean() + " ± " + getMarginOfError(0.95) +
                " (" + getCount() + " samples)";
    }
}
