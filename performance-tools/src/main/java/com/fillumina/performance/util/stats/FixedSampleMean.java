package com.fillumina.performance.util.stats;

/**
 * Returns the mean of the last n values inserted.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class FixedSampleMean {
    private final double[] data;
    private int index;
    private int values;
    private double sum;

    /**
     * @param quantity values to apply the mean to
     */
    public FixedSampleMean(int quantity) {
        this.data = new double[quantity];
    }

    public void addSample(double sample) {
        sum += sample;
        sum -= data[index];
        data[index] = sample;

        final int length = data.length;
        index = (index + 1) % length;
        if (values < length) {
            values++;
        }
    }

    /** Sum of the last n values inserted. */
    public double getSum() {
        return sum;
    }

    /** Mean of the last n values inserted. */
    public double getMean() {
        if (values == 0) {
            throw new RuntimeException("no values");
        }
        return sum / values;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{values=" + values +
                (values > 0 ? ", sum=" + sum + ", mean=" + getMean() : "") + '}';
    }
}
