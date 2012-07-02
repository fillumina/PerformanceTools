package com.fillumina.performance.utils;

import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author fra
 */
public class StatisticsTest {

    // interval [0.1 .. 0.9]
    final double[] values = {0.3, 0.8, 0.2, 0.6, 0.9, 0.4, 0.5, 0.1, 0.7};

    private RunningStatistics stats;

    @Before
    public void initStats() {
        stats = new RunningStatistics();
        stats.addAll(values);
    }

    @Test
    public void shouldGiveTheSum() {
        assertEquals(4.5, stats.sum(), 1E-8);
    }

    @Test
    public void shouldGiveTheAverage() {
        assertEquals(average(values), stats.average(), 1E-8);
    }

    @Test
    public void shouldGiveTheMin() {
        assertEquals(0.1, stats.min(), 1E-8);
    }

    @Test
    public void shouldGiveTheMax() {
        assertEquals(0.9, stats.max(), 1E-8);
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
        assertEquals(standardDeviation(values), stats.standardDeviation(), 1E-8);
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
        final double average = average(data);
        double base = 0;
        for (Double value: data) {
            base += Math.pow(value - average, 2);
        }
        return base / data.length;
    }

}
