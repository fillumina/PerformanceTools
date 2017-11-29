package com.fillumina.performance.util.stats;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class TukeyTest {

    @Test
    public void shouldFindTheMedian() {
        checkTukeyValue(100.0, 100.0);
        checkTukeyValue(1.0, 1.0);
        checkTukeyValue(12.345, 12.345);
    }

    @Test
    public void shouldBeDifferent() {
        checkTukeyValue(10.0, 20.0);
    }

    private void checkTukeyValue(final double meanA, final double meanB) {
        OnlineMeasure a = new OnlineMeasure();
        OnlineMeasure b = new OnlineMeasure();

        for (int i=0; i<50; i++) {
            a.addSample(meanA);
            b.addSample(meanA);
        }

        MultiMeasureSignificance mm = new MultiMeasureSignificance(a, b);
        assertEquals(0.44338410420401253, mm.tukeyKramerHsdPValue(0, 1), 1E-5);
    }


    private static double f1(double d) {
        return Math.abs(d - 0.5) / 0.5;
    }

    public static final double TUKEY_MIDDLE_VALUE = 0.44338410420401253;
    private static double f2(double d) {
        if (d < TUKEY_MIDDLE_VALUE) {
            return 1.0 - (d - 0.1) / (TUKEY_MIDDLE_VALUE - 0.1);
        }
        return (d - TUKEY_MIDDLE_VALUE) / (1.0 - TUKEY_MIDDLE_VALUE);
    }

    public static void main(final String[] args) {
        for (double d=0.1; d<1.1; d+=0.1) {
            System.out.println("" + d + " = " + f1(d) + " -> " + f2(d));
        }
    }
}
