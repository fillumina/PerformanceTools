package com.fillumina.performance.util.stats;

import java.io.Serializable;
import java.util.Locale;
import java.util.Objects;

/**
 * Computes the confidence interval of the ratio of two normal means.
 *
 * @see <a href='http://stats.stackexchange.com/questions/16349/how-to-compute-the-confidence-interval-of-the-ratio-of-two-normal-means'>
 *  StackExchange: How to compute the confidence interval of the decimal of two normal means</a>
 * @see <a href='http://www.graphpad.com/FAQ/images/Ci%20of%20quotient.pdf'>
 *  Harvey J. Motulsky: Confidence Interval of a decimal of two means (PDF)</a>
 * @see <a href='https://en.wikipedia.org/wiki/Fieller%27s_theorem'>
 *  Wikipedia: Fieller's Theorem</a>
 *
 * @author Harvey J. Motulsky
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MeasureRatio extends AbstractConfidenceInterval
        implements ConfidenceInterval, Serializable {
    private static final long serialVersionUID = 1L;

    private final boolean valid;
    private final double ratio;
    private final long count;
    private final double standardError;
    private final double marginOfError;
    private final Ratio confidence;

    public MeasureRatio(Measure a, Measure b, Ratio confidence) {
        this(a.getMean(), a.getVariance(), a.getCount(),
                b.getMean(), b.getVariance(), b.getCount(),
                confidence);
    }

    public MeasureRatio(
            double meanA, double varA, long countA,
            double meanB, double varB, long countB,
            Ratio confidence) {
        Objects.requireNonNull(confidence, "confidence cannot be null");
        this.confidence = confidence;
        count = countA + countB;
        double g = StatFunctions.student(confidence.getDecimal(), count - 2) *
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
                StatFunctions.student(confidence.getDecimal(), count - 2);
    }

    public MeasureRatio(Measure statA, Ratio confidence) {
        this(statA.getVariance(), statA.getCount(), confidence);
    }

    /** To use when A and B measure are the same (ratio will be 1.0). */
    public MeasureRatio(double varA, long countA, Ratio confidence) {
        this.confidence = confidence;
        count = countA;
        valid = true;
        ratio = 1.0;
        standardError = Math.sqrt(varA) / Math.sqrt(countA);
        marginOfError = 0.0;
    }

    /** Is the result valid. */
    public boolean isValid() {
        return valid;
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

    public static boolean isEquals(Measure a, Measure b, Ratio confidence) {
        MeasureRatio mr = new MeasureRatio(a, b, confidence);
        return mr.getLowerBound() <= 1 && 1 <= mr.getUpperBound();
    }

    public static boolean isLowerThan(Measure a, Measure b, Ratio confidence) {
        MeasureRatio mr = new MeasureRatio(a, b, confidence);
        return mr.getUpperBound() < 1;
    }

    public static boolean isGreaterThan(Measure a, Measure b, Ratio confidence) {
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
    public Ratio getConfidence() {
        return confidence;
    }

    /**
     * Standard error of the mean.
     * @see <a href='http://www.sportsci.org/resource/stats/meansd.html'>
     * Standard Error of the mean</a>
     */
    private static double sem(double variance, long samples) {
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
        return Objects.equals(this.confidence, other.confidence);
    }

    public String toStringAsPercentage() {
        if (!valid) {
            return String.format(Locale.US,
                    "%3.2f%% (not statistically valid)", ratio * 100);
        }
        return String.format(Locale.US,
                "%.2f +/- %.2f %%",
                ratio * 100, marginOfError * 100, confidence.getPercentage());
    }

    public String toStringAsPercentageWithConfidence() {
        if (!valid) {
            return String.format(Locale.US,
                    "%3.2f%% (not statistically valid)", ratio * 100);
        }
        return String.format(Locale.US,
                "%.3f +/- %.3f %% (confidence %.3f %%)",
                ratio * 100, marginOfError * 100, confidence.getPercentage());
    }

    public String toAlternativeString() {
        if (ratio <= 1) {
            if (!valid) {
                return String.format(Locale.US, "%.3f %%", ratio * 100);
            }
            return String.format(Locale.US, "%.3f +/- %.3f %%",
                    ratio * 100, marginOfError * 100);
        } else {
            if (!valid) {
                return String.format(Locale.US, "%.5f x", ratio);
            }
            return String.format(Locale.US, "%.5f +/- %.5f x",
                    ratio, marginOfError, confidence);
        }
    }

    @Override
    public String toString() {
        if (!valid) {
            return String.format(Locale.US,"%.3f (not statistically valid with " +
                    " %3.2f%% confidence)",
                    ratio * 100, confidence.getPercentage());
        }
        return String.format(Locale.US,"%.3f +/- %.3f (confidence %3.4f)",
                ratio, marginOfError, confidence.getPercentage());
    }
}

