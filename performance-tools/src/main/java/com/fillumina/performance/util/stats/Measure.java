package com.fillumina.performance.util.stats;

import java.util.Locale;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface Measure {

    default Ratio getAccuracy(Ratio confidence) {
        return Ratio.decimal(getMarginOfError(confidence) / getMean());
    }

    default double getStandardDeviation() {
        return Math.sqrt(getVariance());
    }

    /**
     * Also called standard deviation of the mean.
     * @see <a href='http://www.batesville.k12.in.us/physics/apphynet/Measurement/standard_deviation.htm'>
     *  Standard Dviation</a>
     */
    default double getStandardError() {
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
    default double getUnbiasedStandardDeviation() {
        return Math.sqrt(getUnbiasedVariance());
    }

    default double getMarginOfError(Ratio confidence) {
        return getStandardError() * StatFunctions.zeta(confidence.getDecimal());
    }

    default MarginOfErrorConfidenceInterval getConfidenceInterval(
            Ratio confidence) {
        return new MarginOfErrorConfidenceInterval(getMean(),
                getMarginOfError(confidence), confidence);
    }

    default String toStringForConfidence(Ratio confidence) {
        return String.format(Locale.US, "%,.4f +/- %,.4f (%,d samples)",
            getMean(), getMarginOfError(confidence), getCount());
    }

    /** @return the number of samples. */
    long getCount();

    double getMax();

    double getMean();

    double getMin();

    double getSum();

    /**
     * An unbiased estimator for the getVariance is given by applying Bessel's
     * correction, using N − 1 instead of N to yield the
     * <b>unbiased sample getVariance</b>, denoted s<sup>2</sup>.
     * Most of the time this is the <i>getVariance</i> people is referring to.
     *
     * @see <a href='https://en.wikipedia.org/wiki/Standard_deviation#Corrected_sample_standard_deviation'>
     *  Unbiased Sample Variance</a>
     * @return
     */
    double getUnbiasedVariance();

    /**
     * @see <a href='http://www.math.uah.edu/stat/sample/Variance.html'>
     *  Variance</a>
     * @see #getUnbiasedVariance()
     */
    double getVariance();
}
