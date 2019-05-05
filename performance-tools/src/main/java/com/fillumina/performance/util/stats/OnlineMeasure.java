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
public class OnlineMeasure extends Measure implements Serializable {
    private static final long serialVersionUID = 1L;

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
            addSample(value);
        }
        return this;
    }

    public OnlineMeasure addAll(final Iterable<? extends Number> collection) {
        for (Number value: collection) {
            addSample(value.doubleValue());
        }
        return this;
    }

    public OnlineMeasure addSample(final double value) {
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
     * <b>unbiased sample variance</b>, denoted s<sup>2</sup>.
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

    public void clear() {
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
     *   Wikipedia: Algorithm for calculating getVariance</a>:
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
}
