package com.fillumina.performance.util.stats;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MeasureComparatorTest {
    private static final Ratio P_75 = Ratio.percentage(75);

    private static class MeasureImpl extends FakeMeasure {
        MeasureImpl(double mean, double marginOfError) {
            this.mean = mean;
            this.marginOfError = marginOfError;
        }
    }

    @Test
    public void shouldTestBiggerValue() {
        Measure a = new MeasureImpl(5.3, 0.03);
        Measure b = new MeasureImpl(5.0, 0.02);
        MeasureComparator comparator = new MeasureComparator(P_75);
        int result = comparator.compare(a, b);
        assertEquals(1, result);
    }

    @Test
    public void shouldTestSmallerValue() {
        Measure a = new MeasureImpl(4.3, 0.03);
        Measure b = new MeasureImpl(5.0, 0.02);
        MeasureComparator comparator = new MeasureComparator(P_75);
        int result = comparator.compare(a, b);
        assertEquals(-1, result);
    }

    @Test
    public void shouldTestEqualsValue() {
        Measure a = new MeasureImpl(5.3, 0.3);
        Measure b = new MeasureImpl(5.0, 0.02);
        MeasureComparator comparator = new MeasureComparator(P_75);
        int result = comparator.compare(a, b);
        assertEquals(0, result);
    }

}
