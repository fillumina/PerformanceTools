package com.fillumina.performance.util.stats;

import java.io.Serializable;

/**
 * @see http://www.dummies.com/how-to/content/creating-a-confidence-interval-for-the-difference-.html
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MeasureDifference  extends AbstractConfidenceInterval
        implements ConfidenceInterval, Serializable {

    private static final long serialVersionUID = 1L;
    private final double value;
    private final double standardError;
    private final double marginOfError;
    private final double confidence;

    public MeasureDifference(Measure statA, Measure statB,
            double confidence) {
        this(statA.mean(), statA.variance(), statA.count(),
                statB.mean(), statB.variance(), statB.count(),
                confidence);
    }

    public MeasureDifference(
            double meanA, double varA, long countA,
            double meanB, double varB, long countB,
            double confidence) {
        this.confidence = confidence;
        this.value = meanA - meanB;
        standardError = Math.sqrt(varA / countA + varB / countB);
        marginOfError = standardError * StatFunctions.zeta(confidence);
    }

    @Override
    public double getValue() {
        return value;
    }

    public double getStandardError() {
        return standardError;
    }

    public double getMarginOfError() {
        return marginOfError;
    }

    @Override
    public double getConfidence() {
        return confidence;
    }

    @Override
    public double getLowerBound() {
        return value - marginOfError;
    }

    @Override
    public double getUpperBound() {
        return value + marginOfError;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash =
                29 * hash +
                (int) (Double.doubleToLongBits(this.value) ^
                (Double.doubleToLongBits(this.value) >>> 32));
        hash =
                29 * hash +
                (int) (Double.doubleToLongBits(this.standardError) ^
                (Double.doubleToLongBits(this.standardError) >>> 32));
        hash =
                29 * hash +
                (int) (Double.doubleToLongBits(this.marginOfError) ^
                (Double.doubleToLongBits(this.marginOfError) >>> 32));
        hash =
                29 * hash +
                (int) (Double.doubleToLongBits(this.confidence) ^
                (Double.doubleToLongBits(this.confidence) >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MeasureDifference other = (MeasureDifference) obj;
        if (Double.doubleToLongBits(this.value) !=
                Double.doubleToLongBits(other.value)) {
            return false;
        }
        if (Double.doubleToLongBits(this.standardError) !=
                Double.doubleToLongBits(other.standardError)) {
            return false;
        }
        if (Double.doubleToLongBits(this.marginOfError) !=
                Double.doubleToLongBits(other.marginOfError)) {
            return false;
        }
        if (Double.doubleToLongBits(this.confidence) != Double.doubleToLongBits(other.confidence)) {
            return false;
        }
        return true;
    }

    public String toStringAsPercentage() {
        return String.format("%.5f ± %.5f %% (confidence %3.4f %%)",
                value * 100, marginOfError * 100, confidence * 100);
    }

    @Override
    public String toString() {
        return String.format("%.5f ± %.5f (confidence %3.4f)",
                value, marginOfError, confidence);
    }

}
