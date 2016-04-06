package com.fillumina.performance.stats;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati
 */
public class MeasureTest {

    // interval [0.1 .. 0.9] taken randomly
    final double[] values = {0.3, 0.8, 0.2, 0.6, 0.9, 0.4, 0.5, 0.1, 0.7};

    private RunningMeasure stats;

    @Before
    public void initStats() {
        stats = new RunningMeasure();
        stats.addAll(values);
    }

    @Test
    public void shouldGiveTheSum() {
        assertEquals(4.5, stats.sum(), 0);
    }

    @Test
    public void shouldGiveTheAverage() {
        assertEquals(average(values), stats.mean(), 0);
    }

    @Test
    public void shouldGiveTheMin() {
        assertEquals(0.1, stats.min(), 0);
    }

    @Test
    public void shouldGiveTheMax() {
        assertEquals(0.9, stats.max(), 0);
    }

    @Test
    public void shouldGiveTheCount() {
        assertEquals(9, stats.count());
    }

    @Test
    public void shouldGiveTheVariance() {
        assertEquals(variance(values), stats.variance(), 1E-8);
    }

    @Test
    public void shouldGiveTheStandardDeiviation() {
        assertEquals(standardDeviation(values),
                stats.standardDeviation(), 1E-8);
    }

    @Test
    public void shouldGiveTheUnbiasedVariance() {
        assertEquals(unbiasedVariance(values), stats.unbiasedVariance(), 1E-8);
    }

    @Test
    public void shouldGiveTheUnbiasedStandardDeiviation() {
        assertEquals(unbiasedStandardDeviation(values),
                stats.unbiasedStandardDeviation(), 1E-8);
    }

    // standard (not running) formulas

    private double average(final double... data) {
        double sum = 0;
        for (double x: data) {
            sum += x;
        }
        return sum / data.length;
    }

    private double standardDeviation(final double... data) {
        final double var = variance(data);
        return Math.sqrt(var);
    }

    private double variance(final double... data) {
        double base = quadraticVariation(data);
        return base / data.length;
    }

    private double unbiasedStandardDeviation(final double... data) {
        final double var = unbiasedVariance(data);
        return Math.sqrt(var);
    }

    private double unbiasedVariance(final double... data) {
        double base = quadraticVariation(data);
        return base / (data.length - 1);
    }

    protected double quadraticVariation(final double[] data) {
        final double average = average(data);
        double base = 0;
        for (Double value: data) {
            base += Math.pow(value - average, 2);
        }
        return base;
    }
}
