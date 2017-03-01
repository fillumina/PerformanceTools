package com.fillumina.performance.util.stats;

import com.fillumina.performance.util.formatter.TableFormatter;
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

    private OnlineMeasure stats;

    @Before
    public void initStats() {
        stats = new OnlineMeasure();
        stats.addAll(values);
    }

    @Test
    public void shouldGiveTheSum() {
        assertEquals(4.5, stats.getSum(), 0);
    }

    @Test
    public void shouldGiveTheAverage() {
        assertEquals(average(values), stats.getMean(), 0);
    }

    @Test
    public void shouldGiveTheMin() {
        assertEquals(0.1, stats.getMin(), 0);
    }

    @Test
    public void shouldGiveTheMax() {
        assertEquals(0.9, stats.getMax(), 0);
    }

    @Test
    public void shouldGiveTheCount() {
        assertEquals(9, stats.getCount());
    }

    @Test
    public void shouldGiveTheVariance() {
        assertEquals(variance(values), stats.getVariance(), 1E-8);
    }

    @Test
    public void shouldGiveTheStandardDeiviation() {
        assertEquals(standardDeviation(values),
                stats.getStandardDeviation(), 1E-8);
    }

    @Test
    public void shouldGiveTheUnbiasedVariance() {
        assertEquals(unbiasedVariance(values), stats.getUnbiasedVariance(), 1E-8);
    }

    @Test
    public void shouldGiveTheUnbiasedStandardDeiviation() {
        assertEquals(unbiasedStandardDeviation(values),
                stats.getUnbiasedStandardDeviation(), 1E-8);
    }

    @Test
    public void shouldMarginRisesWithRequiredConfidence() {
        double lastMargin = 0.0;
        TableFormatter tf = new TableFormatter();
        for (double confidence = 0.1; confidence < 1.0; confidence+=0.1) {
            final Ratio p = Ratio.decimal(confidence);
            final MarginOfErrorConfidenceInterval confidenceInterval =
                    stats.getConfidenceInterval(p);
            double margin = confidenceInterval.getMarginOfError();
            assertTrue(lastMargin < margin);
            lastMargin = margin;

            tf.cell(confidence).cell("=").cell(confidenceInterval).endl();
        }
//        System.out.println(tf);
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
