package com.fillumina.performance.util.stats;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ConfidenceIntervalDifferenceTest {

    /**
     * @see http://www.dummies.com/how-to/content/creating-a-confidence-interval-for-the-difference-.html
     */
    @Test
    public void checkExample() {
        ConfidenceIntervalDifference diff = new ConfidenceIntervalDifference(
            8.5, pow2(0.35), 100,
            7.5, pow2(0.45), 110,
            0.95
        );

        assertEquals(0.1085, diff.getMarginOfError(), 1E-3);
    }

    private static double pow2(double x) {
        return x * x;
    }

}
