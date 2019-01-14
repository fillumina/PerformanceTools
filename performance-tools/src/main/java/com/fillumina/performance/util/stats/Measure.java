package com.fillumina.performance.util.stats;

import java.util.Locale;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class Measure {
    public static final double STD_FACTOR = 3.0;

    /** @return the number of samples. */
    public abstract long getCount();

    public abstract double getMax();

    public abstract double getMean();

    public abstract double getMin();

    public abstract double getSum();

    public Measure sum(Measure m) {
        return new MeasureSum(this, m);
    }

    public Measure subtract(Measure m) {
        return new MeasureDifference(this, m);
    }

    public Measure divideBy(double value) {
        return new MeasureTimesValue(this, 1.0/value);
    }

    public Measure multiplyBy(double value) {
        return new MeasureTimesValue(this, value);
    }

    public Measure join(Measure m) {
        return new MeasureJoin(this, m);
    }

    public MeasureRatio ratio(Measure m, Ratio confidence) {
        return new MeasureRatio(this, m, confidence);
    }

    /**
     * An unbiased estimator for the getVariance is given by applying Bessel's
     * correction, using N − 1 instead of N to yield the
     * <b>unbiased sample getVariance</b>, denoted s<sup>2</sup>.
     * Most of the time this is the <i>variance</i> people is referring to.
     *
     * @see <a href='https://en.wikipedia.org/wiki/Standard_deviation#Corrected_sample_standard_deviation'>
     *  Unbiased Sample Variance</a>
     * @return
     */
    public abstract double getUnbiasedVariance();

    /**
     * @see <a href='http://www.math.uah.edu/stat/sample/Variance.html'>
     *  Variance</a>
     * @see #getUnbiasedVariance()
     */
    public abstract double getVariance();

    /**
     *
     * @see <a href='http://www.webassign.net/question_assets/unccolphysmechl1/measurements/manual.html'>
     *  Measurements and Error Analysis</a>
     *
     * @param confidence
     * @return
     */
    public Ratio getFractionalUncertainty(Ratio confidence) {
        return Ratio.decimal(Math.abs(getMarginOfError(confidence) / getMean()));
    }

    public double getStandardDeviation() {
        return Math.sqrt(getVariance());
    }

    /**
     * Also called standard deviation of the mean.
     * @see <a href='http://www.batesville.k12.in.us/physics/apphynet/Measurement/standard_deviation.htm'>
     *  Standard Deviation</a>
     */
    public double getStandardError() {
        return getUnbiasedStandardDeviation() / Math.sqrt(getCount());
    }

    /**
     * While <b>s<sup>2</sup><b> (unbiased sample getVariance) is an unbiased
     * estimator for the population variance, <b>s</b> is still a biased
     * estimator  for the population standard deviation, though markedly
     * less biased than the uncorrected sample standard deviation.
     * The bias is still significant for small samples (N less than 10),
     * and also drops off as 1/N as sample size increases. This estimator is
     * commonly used and generally known simply as the
     * <b>sample standard deviation</b>.
     */
    public double getUnbiasedStandardDeviation() {
        return Math.sqrt(getUnbiasedVariance());
    }

    public double getMarginOfError(Ratio confidence) {
        if (getCount() == 1) {
            return 0; // possibly true, but I'm unsure. Anyway it works.
        }
        return getStandardError() * StatFunctions.zeta(confidence.getDecimal());
    }

    public MarginOfErrorConfidenceInterval getConfidenceInterval(
            Ratio confidence) {
        return new MarginOfErrorConfidenceInterval(getMean(),
                getMarginOfError(confidence), confidence);
    }

    public String toStringForConfidence(Ratio confidence) {
        final long count = getCount();
        final double marginOfError = getMarginOfError(confidence);
        switch ((int)count) {
            case 0:
                return "(no data)";
            case 1:
                return String.format(Locale.US, "%,.4f (1 sample)", getMean());
            default:
                return String.format(Locale.US, "%,.4f +/- %,.4f (%,d samples)",
                    getMean(), marginOfError, count);
        }
    }

    /**
     * Evaluates if the given decimal is to be considered an outliers in the
     * collection. The formula is empirical but widely accepted.
     */
    public boolean isOutlier(double value) {
        return isOutlier(value, STD_FACTOR);
    }

    /**
     * Check if the given decimal is closer than {@param stdFactor} times
     * from the mean. If the {@param stdFactor} is 3 then this represent
     * an accepted formula to discover outliers.
     *
     * @param value     the decimal to check
     * @param stdFactor the factor to multiply to the standard deviation
     * @return          if the decimal lies in the accepted interval
                  for the collection or it is an outlier.
     */
    public boolean isOutlier(double value, double stdFactor) {
        final double stdev = getUnbiasedStandardDeviation();
        return Math.abs(value - getMean()) > stdev * stdFactor;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + Double.hashCode(getCount());
        hash = 67 * hash + Double.hashCode(getSum());
        hash = 67 * hash + Double.hashCode(getMin());
        hash = 67 * hash + Double.hashCode(getMax());
        hash = 67 * hash + Double.hashCode(getVariance());
        hash = 67 * hash + Double.hashCode(getMean());
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
        final Measure other = (Measure) obj;
        if (this.getCount() != other.getCount()) {
            return false;
        }
        if (Double.doubleToLongBits(getSum()) !=
                Double.doubleToLongBits(other.getSum())) {
            return false;
        }
        if (Double.doubleToLongBits(getMax()) !=
                Double.doubleToLongBits(other.getMax())) {
            return false;
        }
        if (Double.doubleToLongBits(getMin()) !=
                Double.doubleToLongBits(other.getMin())) {
            return false;
        }
        if (Double.doubleToLongBits(getVariance()) !=
                Double.doubleToLongBits(other.getVariance())) {
            return false;
        }
        if (Double.doubleToLongBits(getMean()) !=
                Double.doubleToLongBits(other.getMean())) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return toStringForConfidence(Ratio.P_99);
    }
}
