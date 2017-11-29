package com.fillumina.performance.util.stats;

import java.io.Serializable;

/**
 * @see http://www.dummies.com/how-to/content/creating-a-confidence-interval-for-the-difference-.html
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MeasureSum extends Measure implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Measure a;
    private final Measure b;

    public MeasureSum(Measure a, Measure b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public double getStandardError() {
        return Math.sqrt(getUnbiasedVariance());
    }

    @Override
    public long getCount() {
        return (a.getCount() + b.getCount()) / 2;
    }

    @Override
    public double getSum() {
        return a.getSum() + b.getSum();
    }

    @Override
    public double getMean() {
        return a.getMean() + b.getMean();
    }

    @Override
    public double getMax() {
        return a.getMax() + b.getMax();
    }

    @Override
    public double getMin() {
        return a.getMin() + b.getMin();
    }

    @Override
    public double getUnbiasedVariance() {
        return a.getVariance() / (a.getCount() - 1) +
                b.getVariance() / (b.getCount() - 1);
    }

    @Override
    public double getVariance() {
        return a.getVariance() / a.getCount() +
                b.getVariance() / b.getCount();
    }

    @Override
    public String toString() {
        return toStringForConfidence(Ratio.P_99);
    }
}
