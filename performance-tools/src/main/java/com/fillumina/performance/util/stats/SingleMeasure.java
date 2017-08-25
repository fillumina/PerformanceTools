package com.fillumina.performance.util.stats;

import com.fillumina.performance.util.stats.Measure;
import java.io.Serializable;
import java.util.Locale;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SingleMeasure implements Measure, Serializable {
    private static final long serialVersionUID = 1L;
    private final double value;

    public SingleMeasure(double value) {
        this.value = value;
    }

    @Override
    public long getCount() {
        return 1;
    }

    @Override
    public double getMax() {
        return value;
    }

    @Override
    public double getMean() {
        return value;
    }

    @Override
    public double getMin() {
        return value;
    }

    @Override
    public double getSum() {
        return value;
    }

    @Override
    public double getUnbiasedVariance() {
        return 0;
    }

    @Override
    public double getVariance() {
        return 0;
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "%,.4f +/- 0.000 (1 samples)", value);
    }
}
