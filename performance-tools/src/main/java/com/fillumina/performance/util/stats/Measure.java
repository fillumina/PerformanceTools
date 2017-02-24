package com.fillumina.performance.util.stats;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface Measure {

    /** @return the number of samples. */
    long getCount();

    /** @param confidence expressed as a fraction (i.e. 95% -> 0.95) */
    ConfidenceInterval getConfidenceInterval(double confidence);

    /** @param confidence expressed as a fraction (i.e. 95% -> 0.95) */
    double getMarginOfError(double confidence);

    double getMax();

    double getMean();

    double getMin();

    double getStandardDeviation();

    /**
     * Also called standard deviation of the getMean.
     * @see <a href='http://www.batesville.k12.in.us/physics/apphynet/Measurement/standard_deviation.htm'>
     *  Standard Dviation</a>
     */
    double getStandardError();

    double getSum();

    /**
     * While <b>s<sup>2</sup><b> (unbiased sample getVariance) is an unbiased
     * estimator for the population getVariance, <b>s</b> is still a biased
     * estimator  for the population standard deviation, though markedly
     * less biased than the uncorrected sample standard deviation.
     * The bias is still significant for small samples (N less than 10),
     * and also drops off as 1/N as sample size increases. This estimator is
     * commonly used and generally known simply as the
     * <b>sample standard deviation</b>.
     */
    double getUnbiasedStandardDeviation();

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

    /** @param confidence expressed as a fraction (i.e. 95% -> 0.95) */
    String toStringForConfidence(double confidence);
}
