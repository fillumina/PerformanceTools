package com.fillumina.performance.util.stats;

import java.io.Serializable;
import java.util.Locale;
import java.util.Objects;

/**
 * @see http://www.dummies.com/how-to/content/creating-a-confidence-interval-for-the-difference-.html
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ConfidenceIntervalDifference extends AbstractConfidenceInterval
        implements ConfidenceInterval, Serializable {

    private static final long serialVersionUID = 1L;
    private final double value;
    private final double standardError;
    private final double marginOfError;
    private final Ratio confidence;

    public ConfidenceIntervalDifference(Measure statA, Measure statB,
            Ratio confidence) {
        this(statA.getMean(), statA.getVariance(), statA.getCount(),
                statB.getMean(), statB.getVariance(), statB.getCount(),
                confidence);
    }

    public ConfidenceIntervalDifference(
            double meanA, double varA, long countA,
            double meanB, double varB, long countB,
            Ratio confidence) {
        Objects.requireNonNull(confidence, "confidence cannot be null");
        this.confidence = confidence;
        this.value = meanA - meanB;
        standardError = Math.sqrt(varA / countA + varB / countB);
        marginOfError = standardError *
                StatFunctions.zeta(confidence.getDecimal());
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
    public Ratio getConfidence() {
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
        hash = 29 * hash + Objects.hashCode(this.confidence);
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
        final ConfidenceIntervalDifference other = (ConfidenceIntervalDifference) obj;
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
        return Objects.equals(this.confidence, other.confidence);
    }

    public String toStringAsPercentage() {
        return String.format(Locale.US,
                "%.5f +/- %.5f %% (confidence %3.4f %%)",
                value * 100, marginOfError * 100, confidence.getDecimal());
    }

    @Override
    public String toString() {
        return String.format(Locale.US,
                "%.5f +/- %.5f (confidence %3.4f)",
                value, marginOfError, confidence);
    }

}
