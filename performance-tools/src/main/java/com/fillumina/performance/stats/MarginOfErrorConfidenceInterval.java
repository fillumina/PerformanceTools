package com.fillumina.performance.stats;

import java.io.Serializable;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MarginOfErrorConfidenceInterval
        implements ConfidenceInterval, Serializable {
    private static final long serialVersionUID = 1L;
    private final double value;
    private final double marginOfError;
    private final double confidence;

    public MarginOfErrorConfidenceInterval(double value,
            double marginOfError,
            double confidence) {
        this.value = value;
        this.marginOfError = marginOfError;
        this.confidence = confidence;
    }

    @Override
    public double getValue() {
        return value;
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
    public double getConfidence() {
        return confidence;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash +
                (int) (Double.doubleToLongBits(this.value) ^
                (Double.doubleToLongBits(this.value) >>> 32));
        hash = 67 * hash +
                (int) (Double.doubleToLongBits(this.marginOfError) ^
                (Double.doubleToLongBits(this.marginOfError) >>> 32));
        hash = 67 * hash +
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
        final MarginOfErrorConfidenceInterval other
                = (MarginOfErrorConfidenceInterval) obj;
        if (Double.doubleToLongBits(this.value) !=
                Double.doubleToLongBits(other.value)) {
            return false;
        }
        if (Double.doubleToLongBits(this.marginOfError) !=
                Double.doubleToLongBits(other.marginOfError)) {
            return false;
        }
        return (Double.doubleToLongBits(this.confidence) !=
                Double.doubleToLongBits(other.confidence));
    }

    public double getMarginOfError() {
        return marginOfError;
    }

    @Override
    public String toString() {
        return String.format("%.5f ± %.5f%% (confidence %3.2f%%)",
                value, marginOfError, confidence * 100);
    }
}
