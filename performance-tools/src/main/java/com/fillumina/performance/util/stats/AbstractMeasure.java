package com.fillumina.performance.util.stats;

import java.util.Locale;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class AbstractMeasure implements Measure {

    @Override
    public double getStandardDeviation() {
        return Math.sqrt(getVariance());
    }

    /**
     * Also called standard deviation of the mean.
     * @see <a href='http://www.batesville.k12.in.us/physics/apphynet/Measurement/standard_deviation.htm'>
     *  Standard Dviation</a>
     */
    @Override
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
    @Override
    public double getUnbiasedStandardDeviation() {
        return Math.sqrt(getUnbiasedVariance());
    }

    @Override
    public double getMarginOfError(Ratio confidence) {
        return getStandardError() * StatFunctions.zeta(confidence.getDecimal());
    }

    @Override
    public MarginOfErrorConfidenceInterval getConfidenceInterval(
            Ratio confidence) {
        return new MarginOfErrorConfidenceInterval(getMean(),
                getMarginOfError(confidence), confidence);
    }

    @Override
    public String toStringForConfidence(Ratio confidence) {
        return String.format(Locale.US, "%.4f +/- %.4f (%d samples)",
            getMean(), getMarginOfError(confidence), getCount());
    }
}
