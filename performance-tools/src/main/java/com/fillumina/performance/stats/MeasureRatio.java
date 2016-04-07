package com.fillumina.performance.stats;

import java.io.Serializable;

/**
 * Computes the confidence interval of the ratio of two normal means.
 *
 * @see <a href='http://stats.stackexchange.com/questions/16349/how-to-compute-the-confidence-interval-of-the-ratio-of-two-normal-means'>
 *  StackExchange: How to compute the confidence interval of the ratio of two normal means</a>
 * @see <a href='http://www.graphpad.com/FAQ/images/Ci%20of%20quotient.pdf'>
 *  Harvey J. Motulsky: Confidence Interval of a ratio of two means (PDF)</a>
 * @see <a href='https://en.wikipedia.org/wiki/Fieller%27s_theorem'>
 *  Wikipedia: Fieller's Theorem</a>
 *
 * @author Harvey J. Motulsky
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MeasureRatio implements ConfidenceInterval, Serializable {
    private static final long serialVersionUID = 1L;

    private final boolean valid;
    private final double ratio;
    private final long count;
    private final double standardError;
    private final double marginOfError;
    private final double confidence;

    public MeasureRatio(Measure statA, Measure statB,
            double confidence) {
        this(statA.mean(), statA.variance(), statA.count(),
                statB.mean(), statB.variance(), statB.count(),
                confidence);
    }

    public MeasureRatio(
            double meanA, double varA, long countA,
            double meanB, double varB, long countB,
            double confidence) {
        this.confidence = confidence;
        count = countA + countB;
        double g = StatFunctions.student(confidence, count - 2) *
                sem(varB, countB) / meanB;
        g *= g;
        valid = g < 1;
        if (!valid) {
            ratio = meanA / meanB;
            standardError = -1;
            marginOfError = -1;
            return;
        }
        ratio = meanA / (meanB * (1 - g));
        double semA = sem(varA, countA);
        double semB = sem(varB, countB);
        standardError = ratio * Math.sqrt(
                ((1 - g) * (semA * semA) / (meanA * meanA) +
                (semB * semB) / (meanB * meanB)));
        marginOfError = standardError *
                StatFunctions.student(confidence, count - 2);
    }

    /** Is the result valid. */
    public boolean isValid() {
        return valid;
    }

    public double getRatio() {
        return ratio;
    }

    public long getCount() {
        return count;
    }

    public double getStandardError() {
        return standardError;
    }

    public double getMarginOfError() {
        return marginOfError;
    }

    public static boolean isEquals(Measure a, Measure b, double confidence) {
        MeasureRatio mr = new MeasureRatio(a, b, confidence);
        return mr.getLowerBound() <= 1 && 1 <= mr.getUpperBound();
    }

    public static boolean isLowerThan(Measure a, Measure b, double confidence) {
        MeasureRatio mr = new MeasureRatio(a, b, confidence);
        return mr.getUpperBound() < 1;
    }

    public static boolean isGreaterThan(Measure a, Measure b, double confidence) {
        MeasureRatio mr = new MeasureRatio(a, b, confidence);
        return mr.getLowerBound() > 1;
    }

    public int compare() {
        if (getUpperBound() < 1) {
            return -1;
        } else if (getLowerBound() > 1) {
            return 1;
        }
        return 0;
    }

    @Override
    public double getValue() {
        return ratio;
    }

    @Override
    public double getLowerBound() {
        return ratio - marginOfError;
    }

    @Override
    public double getUpperBound() {
        return ratio + marginOfError;
    }

    @Override
    public double getConfidence() {
        return confidence;
    }

    /**
     * Standard error of the mean.
     * @see <a href='http://www.sportsci.org/resource/stats/meansd.html'>
     *  Standard Error of the mean</a>
     */
    private double sem(double variance, long samples) {
        return Math.sqrt(variance / samples);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + (this.valid ? 1 : 0);
        hash = 29 * hash +
                (int) (Double.doubleToLongBits(this.ratio) ^
                (Double.doubleToLongBits(this.ratio) >>> 32));
        hash = 29 * hash + (int) (this.count ^ (this.count >>> 32));
        hash = 29 * hash +
                (int) (Double.doubleToLongBits(this.marginOfError) ^
                (Double.doubleToLongBits(this.marginOfError) >>> 32));
        hash = 29 * hash +
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
        final MeasureRatio other = (MeasureRatio) obj;
        if (this.valid != other.valid) {
            return false;
        }
        if (Double.doubleToLongBits(this.ratio) !=
                Double.doubleToLongBits(other.ratio)) {
            return false;
        }
        if (this.count != other.count) {
            return false;
        }
        if (Double.doubleToLongBits(this.marginOfError) !=
                Double.doubleToLongBits(other.marginOfError)) {
            return false;
        }
        return Double.doubleToLongBits(this.confidence) !=
                Double.doubleToLongBits(other.confidence);
    }

    public String toStringAsPercentage() {
        if (!valid) {
            return String.format("%3.2f%% (not statistically valid)", ratio * 100);
        }
        return String.format("%3.2f%% ± %3.4f%% (confidence %3.2f%%)",
                ratio * 100, marginOfError * 100, confidence * 100);
    }

    @Override
    public String toString() {
        if (!valid) {
            return String.format("%.4f (not statistically valid with " +
                    " %3.2f%% confidence)",
                    ratio * 100, confidence * 100);
        }
        return String.format("%.5f ± %.5f%% (confidence %3.2f%%)",
                ratio, marginOfError, confidence * 100);
    }
}

