package com.fillumina.performance.util.stats;

import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class NormalDistributionMeasureBuilderTest {
    private static final double TOLERANCE = 0.2;

    @Test
    public void shouldCreateAMeasure() {
        assertMeasure(10, 5, TOLERANCE, 100);
        assertMeasure(33, 3, TOLERANCE, 100);
        assertMeasure(1, 0.05, TOLERANCE, 100);
    }

    @Test(timeout = 1000)
    public void shouldProduceAMeasureWith0StdDev() {
        assertMeasure(10, 0.1, TOLERANCE, 100);
    }

    private void assertMeasure(double mean, double stdev, double tolerance,
            int minSamples) {
        Measure m = new NormalDistributionMeasureBuilder(
                mean, stdev, tolerance, minSamples).build();
        assertEquals(mean, m.getMean(), mean * TOLERANCE);
        assertEquals(stdev, m.getStandardDeviation(), stdev * TOLERANCE);
        assertTrue(m.getCount() >= minSamples);
    }

    @Test
    public void shouldIterateWithNormalDistribution() {
        assertIterator(10, 5, TOLERANCE, 100);
        assertIterator(33, 3, TOLERANCE, 100);
        assertIterator(1, 0.05, TOLERANCE, 100);
        assertIterator(10, 0.1, TOLERANCE, 100);
    }

    private void assertIterator(double mean, double stdev, double tolerance,
            int minSamples) {
        List<Double> list = new ArrayList<>();
        for (double d : new NormalDistributionMeasureBuilder(
                                mean, stdev, tolerance, minSamples)) {
            list.add(d);
        }
        OnlineMeasure m = new OnlineMeasure(list);
        assertEquals(mean, m.getMean(), mean * TOLERANCE);
        assertEquals(stdev, m.getStandardDeviation(), stdev * TOLERANCE);
        assertTrue(m.getCount() >= minSamples);
    }

    public static void main(final String[] args) {
        Measure m = new NormalDistributionMeasureBuilder(10, 5, TOLERANCE, 100)
                .build();
        System.out.println(m);
    }

    @Test
    public void shouldBeEqualsWithTolerance() {
        assertTrue(NormalDistributionMeasureBuilder.aboutEquals(10, 10, 0));
        assertTrue(NormalDistributionMeasureBuilder.aboutEquals(10, 11, 0.2));
        assertTrue(NormalDistributionMeasureBuilder.aboutEquals(11, 10, 0.2));
        assertFalse(NormalDistributionMeasureBuilder.aboutEquals(11, 10, 1E-5));
        assertFalse(NormalDistributionMeasureBuilder.aboutEquals(20, 10, 0.2));
    }
}
