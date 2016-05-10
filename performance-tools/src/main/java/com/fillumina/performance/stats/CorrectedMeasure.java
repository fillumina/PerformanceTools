package com.fillumina.performance.stats;

import com.fillumina.performance.util.stats.ConfidenceInterval;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.MeasureDifference;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
class CorrectedMeasure implements Measure {
    private final Measure baseMeasure;
    private final MeasureDifference correctedMean;

    public CorrectedMeasure(Measure baseMeasure,
            Measure correction, double confidence) {
        this.baseMeasure = baseMeasure;
        this.correctedMean =
                new MeasureDifference(baseMeasure, correction, confidence);
    }

    @Override
    public double mean() {
        return correctedMean.getValue();
    }

    @Override
    public ConfidenceInterval getConfidenceInterval(double confidence) {
        return correctedMean;
    }

    @Override
    public double marginOfError(double confidence) {
        return correctedMean.getMarginOfError();
    }

    @Override
    public double standardError() {
        return correctedMean.getStandardError();
    }

    @Override
    public String toStringForConfidence(double confidence) {
        return correctedMean.getValue() + " ± " + marginOfError(0.95) +
                " (" + count() + " samples)";
    }

    @Override
    public long count() {
        return baseMeasure.count();
    }

    @Override
    public double max() {
        return baseMeasure.max();
    }

    @Override
    public double min() {
        return baseMeasure.min();
    }

    @Override
    public double standardDeviation() {
        return baseMeasure.standardDeviation();
    }

    @Override
    public double sum() {
        return baseMeasure.sum();
    }

    @Override
    public double unbiasedStandardDeviation() {
        return baseMeasure.unbiasedStandardDeviation();
    }

    @Override
    public double unbiasedVariance() {
        return baseMeasure.unbiasedVariance();
    }

    @Override
    public double variance() {
        return baseMeasure.variance();
    }

}
