package com.fillumina.performance.util.stats;

import java.io.Serializable;

/**
 * @see http://www.dummies.com/how-to/content/creating-a-confidence-interval-for-the-difference-.html
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MeasureDifference implements Measure, Serializable {
    private static final long serialVersionUID = 1L;

    private final Measure statA;
    private final Measure statB;

    public MeasureDifference(Measure statA, Measure statB) {
        this.statA = statA;
        this.statB = statB;
    }

    @Override
    public double getStandardError() {
        return Math.sqrt(getUnbiasedVariance());
    }

    @Override
    public long getCount() {
        return (statA.getCount() + statB.getCount()) / 2;
    }

    @Override
    public ConfidenceInterval getConfidenceInterval(Ratio confidence) {
        return new MarginOfErrorConfidenceInterval(getMean(),
                getMarginOfError(confidence), confidence);
    }

    @Override
    public double getMarginOfError(Ratio confidence) {
        return getStandardError() * StatFunctions.zeta(confidence.getValue());
    }

    @Override
    public double getSum() {
        return statA.getSum() - statB.getSum();
    }

    @Override
    public double getMean() {
        return statA.getMean() - statB.getMean();
    }

    @Override
    public double getMax() {
        return statA.getMax() - statB.getMax();
    }

    @Override
    public double getMin() {
        return statA.getMin() - statB.getMin();
    }

    @Override
    public double getUnbiasedStandardDeviation() {
        return Math.sqrt(getUnbiasedVariance());
    }

    @Override
    public double getUnbiasedVariance() {
        return statA.getVariance() / (statA.getCount() - 1) +
                statB.getVariance() / (statB.getCount() - 1);
    }

    @Override
    public double getStandardDeviation() {
        return Math.sqrt(getVariance());
    }

    @Override
    public double getVariance() {
        return statA.getVariance() / statA.getCount() +
                statB.getVariance() / statB.getCount();
    }


    @Override
    public String toStringForConfidence(Ratio confidence) {
        return getMean() + " +/- " + getMarginOfError(confidence) +
                " (" + getCount() + " samples)";
    }

    @Override
    public String toString() {
        return getMean() + " +/- " + getMarginOfError(Ratio.P_95) +
                " (" + getCount() + " samples)";
    }
}
