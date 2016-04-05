package com.fillumina.performance.stats;

import java.io.Serializable;
import java.util.Collection;

/**
 * Calculates statistics over a set of data.
 * The values are not retained and all statistics
 * are calculated on the fly so its memory footprint is fixed whatever amount
 * of data is collected. This class is immutable.
 *
 * @author Francesco Illuminati
 */
public class Measure implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final Measure EMPTY = new Measure();

    private long count;
    private double sum;
    private double max = Double.MIN_VALUE;
    private double min = Double.MAX_VALUE;
    private double M2, mean;

    private Measure() {
        count = 0;
        sum = 0;
        max = 0;
        min = 0;
        M2 = 0;
        mean = 0;
    }

    public Measure(final double... values) {
        addAll(values);
    }

    public Measure(final Collection<? extends Number> collection) {
        addAll(collection);
    }

    public Measure(final Measure statistics) {
        this.count = statistics.count;
        this.sum = statistics.sum;
        this.max = statistics.max;
        this.min = statistics.min;
        this.M2 = statistics.M2;
        this.mean = statistics.mean;
    }

    protected Measure addAll(final double... values) {
        for (double value: values) {
            add(value);
        }
        return this;
    }

    protected Measure addAll(final Iterable<? extends Number> collection) {
        for (Number value: collection) {
            add(value.doubleValue());
        }
        return this;
    }

    protected Measure add(final double value) {
        count++;
        sum += value;
        if (value > max) {
            max = value;
        }
        if (value < min) {
            min = value;
        }
        calculateVariance(value);
        return this;
    }

    public double max() {
        assertDataPresent();
        return max;
    }

    public double min() {
        assertDataPresent();
        return min;
    }

    public long count() {
        return count;
    }

    public double sum() {
        assertDataPresent();
        return sum;
    }

    public double mean() {
        assertDataPresent();
        return mean;
    }

    /**
     * @see <a href='http://www.math.uah.edu/stat/sample/Variance.html'>
     *  Variance</a>
     * @see #unbiasedVariance() 
     */
    public double variance() {
        assertDataPresent();
        return M2 / count;
    }

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
    public double unbiasedVariance() {
        assertDataPresent();
        return M2 / (count - 1);
    }

    public double standardDeviation() {
        assertDataPresent();
        return Math.sqrt(variance());
    }

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
    public double unbiasedStandardDeviation() {
        assertDataPresent();
        return Math.sqrt(unbiasedVariance());
    }

    /**
     * Also called standard deviation of the mean.
     * @see <a href='http://www.batesville.k12.in.us/physics/apphynet/Measurement/standard_deviation.htm'>
     *  Standard Dviation</a>
     */
    public double standardError() {
        return unbiasedStandardDeviation() / Math.sqrt(count());
    }

    public double marginOfError(double confidence) {
        return standardError() * StatFunctions.zeta(confidence);
    }

    protected void clear() {
        count = 0;
        sum = 0;
        min = Double.MAX_VALUE;
        max = Double.MIN_VALUE;
        M2 = 0;
        mean = 0;
    }

    /**
     * This is a running algorithm to calculate the variance.
     * See
     * <a href='http://en.wikipedia.org/wiki/Algorithms_for_calculating_variance'>
     * Wikipedia: Algorithm for calculating variance</a>:
     * <code><pre>
        def online_variance(data):
            n = 0
            mean = 0
            M2 = 0

            for x in data:
                n = n + 1
                delta = x - mean
                mean = mean + delta/n
                M2 = M2 + delta*(x - mean)

            variance_n = M2/n
            variance = M2/(n - 1)
            return (variance, variance_n)
    * </pre></code>
    */
    private void calculateVariance(final double x) {
        final double delta = x - mean;
        mean += delta / count;
        M2 += delta * (x - mean);
    }

    private void assertDataPresent() {
        if (count == 0) {
            throw new IllegalStateException("No data recorded");
        }
    }

    @Override
    public String toString() {
        return mean + " ± " + marginOfError(0.95) +
                " (" + count + " samples)";
    }
}
