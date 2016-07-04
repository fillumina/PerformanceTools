package com.fillumina.performance.util.stats;

import com.fillumina.performance.util.Builder;
import static java.lang.Math.*;
import java.util.Iterator;
import java.util.Random;

/**
 * Builds a {@link Measure} with the specified characteristics and returns
 * a {@link Iterator} of doubles following the normal distribution.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class NormalDistributionMeasureBuilder
        implements Builder<Measure>, Iterable<Double> {
    private final double mean;
    private final double stdev;
    private final double tolerance;
    private final int minNumberOfSamples;
    private final Random r = new Random(System.nanoTime());

    /**
     *
     * @param mean          the required mean
     * @param stdev         the required standard deviation
     * @param minNumberOfSamples     how many values should be created at least
     */
    public NormalDistributionMeasureBuilder(double mean,
            double stdev,
            double tolerance,
            int minNumberOfSamples) {
        this.mean = mean;
        this.stdev = stdev;
        this.tolerance = tolerance;
        this.minNumberOfSamples = minNumberOfSamples;
    }

    @Override
    public Measure build() {
        NormalMeasureIterator it = new NormalMeasureIterator();
        while(it.hasNext()) {
            it.next();
        }
        return it.getMeasure();
    }

    /**
     * Iterates through a sequence of at least {@code minNumberOfSamples}
     * values with a normal distribution. It ends when the requirements are
     * satisfied with a 1% accuracy.
     * <p>
     * Note that {@link Iterator#next()} can be called even if
     * {@link Iterator#hasNext() } is false (meaning that the sequence
     * is conform to its requirements).
     */
    @Override
    public Iterator<Double> iterator() {
        return new NormalMeasureIterator();
    }

    private class NormalMeasureIterator implements Iterator<Double> {
        private final OnlineMeasure m = new OnlineMeasure();
        private int index = 0;

        public OnlineMeasure getMeasure() {
            return m;
        }

        @Override
        public boolean hasNext() {
            return index < minNumberOfSamples ||
                    !aboutEquals(m.getMean(), mean, tolerance) ||
                    !aboutEquals(m.getStandardDeviation(), stdev, tolerance);
        }

        /** Can be called even if {@link hasNext()} is false */
        @Override
        public Double next() {
            double value = (r.nextGaussian() * stdev) + mean;
            m.add(value);
            index++;
            return value;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported.");
        }
    }

    static boolean aboutEquals(double a, double b, double tolerance) {
        return abs(a - b) / abs(b) <= tolerance;
    }
}
