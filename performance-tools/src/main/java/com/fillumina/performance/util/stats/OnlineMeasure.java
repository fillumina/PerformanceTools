package com.fillumina.performance.util.stats;

import java.io.Serializable;
import java.util.Collection;

/**
 * Calculates statistics over a set of data.
 * The values are not retained and all statistics
 * are calculated on the fly so its memory footprint is fixed whatever amount
 * of data is collected.
 *
 * @author Francesco Illuminati
 */
public class OnlineMeasure implements Measure, Serializable {
    private static final long serialVersionUID = 1L;
    private final double STD_FACTOR = 3.0;

    private long count;
    private double sum;
    private double max = Double.MIN_VALUE;
    private double min = Double.MAX_VALUE;
    private double M2, mean;

    public OnlineMeasure() {
    }

    public OnlineMeasure(final double... values) {
        addAll(values);
    }

    public OnlineMeasure(final Collection<? extends Number> collection) {
        addAll(collection);
    }

    /** Clone constructor */
    public OnlineMeasure(final Measure other) {
        this.count = other.getCount();
        this.sum = other.getSum();
        this.max = other.getMax();
        this.min = other.getMin();
        this.M2 = other.getVariance() * other.getCount();
        this.mean = other.getMean();
    }

    public OnlineMeasure addAll(final double... values) {
        for (double value: values) {
            add(value);
        }
        return this;
    }

    public OnlineMeasure addAll(final Iterable<? extends Number> collection) {
        for (Number value: collection) {
            add(value.doubleValue());
        }
        return this;
    }

    public OnlineMeasure addIfNotOutlier(final double value) {
        return addIfNotOutlier(value, STD_FACTOR);
    }

    public OnlineMeasure addIfNotOutlier(final double value,
            final double stdevFactor) {
        if (!isOutlier(value, stdevFactor)) {
            add(value);
        }
        return this;
    }

    public OnlineMeasure add(final double value) {
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

    @Override
    public double getMax() {
        return max;
    }

    @Override
    public double getMin() {
        return min;
    }

    /** @return the number of samples. */
    @Override
    public long getCount() {
        return count;
    }

    @Override
    public double getSum() {
        return sum;
    }

    @Override
    public double getMean() {
        return mean;
    }

    /**
     * @see <a href='http://www.math.uah.edu/stat/sample/Variance.html'>
     *  Variance</a>
     * @see #getUnbiasedVariance()
     */
    @Override
    public double getVariance() {
        return M2 / count;
    }

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
    @Override
    public double getUnbiasedVariance() {
        if (count == 1) {
            return 0;
        }
        return M2 / (count - 1);
    }

    @Override
    public double getStandardDeviation() {
        return Math.sqrt(getVariance());
    }

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
    @Override
    public double getUnbiasedStandardDeviation() {
        return Math.sqrt(getUnbiasedVariance());
    }

    /**
     * Also called standard deviation of the getMean.
     * @see <a href='http://www.batesville.k12.in.us/physics/apphynet/Measurement/standard_deviation.htm'>
     *  Standard Dviation</a>
     */
    @Override
    public double getStandardError() {
        return getUnbiasedStandardDeviation() / Math.sqrt(getCount());
    }

    @Override
    public double getMarginOfError(double confidence) {
        return getStandardError() * StatFunctions.zeta(confidence);
    }

    @Override
    public MarginOfErrorConfidenceInterval getConfidenceInterval(
            double confidence) {
        return new MarginOfErrorConfidenceInterval(mean,
                getMarginOfError(confidence), confidence);
    }

    /**
     * Evaluates if the given value is to be considered an outliers in the
     * collection. The formula is empirical but widely accepted.
     */
    public boolean isOutlier(double value) {
        return isOutlier(value, STD_FACTOR);
    }

    /**
     * Check if the given value is closer than {@param stdFactor} times
     * from the mean. If the {@param stdFactor} is 3 then this represent
     * an accepted formula to discover outliers.
     *
     * @param value     the value to check
     * @param stdFactor the factor to multiply to the standard deviation
     * @return          if the value lies in the accepted interval
     *                  for the collection or it is an outlier.
     */
    public boolean isOutlier(double value, double stdFactor) {
        final double stdev = getUnbiasedStandardDeviation();
        return Math.abs(value - mean) > stdev * stdFactor;
    }

    public void clear() {
        count = 0;
        sum = 0;
        min = Double.MAX_VALUE;
        max = Double.MIN_VALUE;
        M2 = 0;
        mean = 0;
    }

    /**
     * This is a running algorithm to calculate the getVariance.
     * See
     * <a href='http://en.wikipedia.org/wiki/Algorithms_for_calculating_variance'>
     *   Wikipedia: Algorithm for calculating getVariance</a>:
     * <code><pre>
        def online_variance(data):
            n = 0
            getMean = 0
            M2 = 0

            for x in data:
                n = n + 1
                delta = x - getMean
                getMean = getMean + delta/n
                M2 = M2 + delta*(x - getMean)

            variance_n = M2/n
            getVariance = M2/(n - 1)
            return (getVariance, variance_n)
    * </pre></code>
    */
    private void calculateVariance(final double x) {
        final double delta = x - mean;
        mean += delta / count;
        M2 += delta * (x - mean);
    }

    @Override
    public String toStringForConfidence(double confidence) {
        return String.format("%.4f ± %.4f (%d samples)",
            mean, getMarginOfError(confidence), count);
    }

    @Override
    public String toString() {
        return toStringForConfidence(0.95);
    }
}
