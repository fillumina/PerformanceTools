package com.fillumina.performance.util.stats;

import java.io.Serializable;

/**
 * @see https://en.wikipedia.org/wiki/Variance
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MeasureTimesValue extends Measure implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Measure m;
    private final double value;

    public MeasureTimesValue(Measure m, double value) {
        this.m = m;
        this.value = value;
    }

    @Override
    public double getStandardError() {
        return Math.sqrt(getUnbiasedVariance());
    }

    @Override
    public long getCount() {
        return m.getCount();
    }

    @Override
    public double getSum() {
        return m.getSum() * value;
    }

    @Override
    public double getMean() {
        return m.getMean() * value;
    }

    @Override
    public double getMax() {
        return m.getMax() * value;
    }

    @Override
    public double getMin() {
        return m.getMin() * value;
    }

    @Override
    public double getUnbiasedVariance() {
        return value * value * m.getVariance() / (m.getCount() - 1);
    }

    @Override
    public double getVariance() {
        return value * value * m.getVariance() / m.getCount();
    }

    @Override
    public String toString() {
        return toStringForConfidence(Ratio.P_99);
    }
}
