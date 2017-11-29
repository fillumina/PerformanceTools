package com.fillumina.performance.util.stats;

import java.io.Serializable;

/**
 *
 * @see
 * @see https://stackoverflow.com/questions/1480626/merging-two-statistical-result-sets
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MeasureJoin extends Measure implements Serializable {
    private static final long serialVersionUID = 1L;

    private final long count;
    private final double sum;
    private final double min;
    private final double max;
    private final double mean;
    private final double M2;

    public MeasureJoin(Measure... measures) {
        double lsum = 0.0;
        int lcount = 0;
        double lmin = Double.MAX_VALUE;
        double lmax = Double.MIN_VALUE;
        double partialMean = 0;
        double partialVar = 0;
        for (Measure m : measures) {
            lsum += m.getSum();
            lcount += m.getCount();
            partialMean += m.getMean() * m.getCount();
            partialVar += (m.getVariance() + m.getMean() *
                    m.getMean()) * m.getCount();
            if (lmin > m.getMin()) {
                lmin = m.getMin();
            }
            if (lmax < m.getMax()) {
                lmax = m.getMax();
            }
        }
        this.count = lcount;
        this.min = lmin;
        this.max = lmax;
        this.sum = lsum;
        this.mean = partialMean / lcount;
        double var = partialVar / lcount - this.mean * this.mean;
        this.M2 = var * lcount;
    }

    @Override
    public long getCount() {
        return count;
    }

    @Override
    public double getMax() {
        return max;
    }

    @Override
    public double getMean() {
        return mean;
    }

    @Override
    public double getMin() {
        return min;
    }

    @Override
    public double getSum() {
        return sum;
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
}
