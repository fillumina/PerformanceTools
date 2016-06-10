package com.fillumina.performance.util.stats;

import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class NormalDistributionMeasureBuilderTest {

    @Test
    public void shouldCreateAMeasure() {
        assertMeasure(10, 5, 100);
        assertMeasure(33, 3, 100);
        assertMeasure(1, 0.05, 100);
    }

    @Test(timeout = 1000)
    public void shouldProduceAMeasureWith0StdDev() {
        assertMeasure(10, 0, 100);
    }

    private void assertMeasure(double mean, double stdev, int minSamples) {
        Measure m = new NormalDistributionMeasureBuilder(mean, stdev, minSamples)
                .build();
        assertEquals(mean, m.getMean(), mean / 100.0);
        assertEquals(stdev, m.getStandardDeviation(), stdev / 100.0);
        assertTrue(m.getCount() >= minSamples);
    }

    @Test
    public void shouldIterateWithNormalDistribution() {
        assertIterator(10, 5, 100);
        assertIterator(33, 3, 100);
        assertIterator(1, 0.05, 100);
        assertIterator(10, 0, 100);
    }

    private void assertIterator(double mean, double stdev, int minSamples) {
        List<Double> list = new ArrayList<>();
        for (double d :
                new NormalDistributionMeasureBuilder(mean, stdev, minSamples)) {
            list.add(d);
        }
        OnlineMeasure m = new OnlineMeasure(list);
        assertEquals(mean, m.getMean(), mean / 100.0);
        assertEquals(stdev, m.getStandardDeviation(), stdev / 100.0);
        assertTrue(m.getCount() >= minSamples);
    }

    public static void main(final String[] args) {
        Measure m = new NormalDistributionMeasureBuilder(10, 5, 100).build();
        System.out.println(m);
    }
}
