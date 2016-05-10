package com.fillumina.performance.util.stats;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
//TODO adhere to the getter conventions (use getValue() instead of value())
public interface Measure {

    /** @return the number of samples. */
    long count();

    ConfidenceInterval getConfidenceInterval(double confidence);

    double marginOfError(double confidence);

    double max();

    double mean();

    double min();

    double standardDeviation();

    /**
     * Also called standard deviation of the mean.
     * @see <a href='http://www.batesville.k12.in.us/physics/apphynet/Measurement/standard_deviation.htm'>
     *  Standard Dviation</a>
     */
    double standardError();

    double sum();

    /**
     * While <b>s<sup>2</sup><b> (unbiased sample variance) is an unbiased
     * estimator for the population variance, <b>s</b> is still a biased
     * estimator  for the population standard deviation, though markedly
     * less biased than the uncorrected sample standard deviation.
     * The bias is still significant for small samples (N less than 10),
     * and also drops off as 1/N as sample size increases. This estimator is
     * commonly used and generally known simply as the
     * <b>sample standard deviation</b>.
     */
    double unbiasedStandardDeviation();

    /**
     * An unbiased estimator for the variance is given by applying Bessel's
     * correction, using N − 1 instead of N to yield the
     * <b>unbiased sample variance</b>, denoted s<sup>2</sup>.
     * Most of the time this is the <i>variance</i> people is referring to.
     *
     * @see <a href='https://en.wikipedia.org/wiki/Standard_deviation#Corrected_sample_standard_deviation'>
     *  Unbiased Sample Variance</a>
     * @return
     */
    double unbiasedVariance();

    /**
     * @see <a href='http://www.math.uah.edu/stat/sample/Variance.html'>
     *  Variance</a>
     * @see #unbiasedVariance()
     */
    double variance();

    String toStringForConfidence(double confidence);
}
