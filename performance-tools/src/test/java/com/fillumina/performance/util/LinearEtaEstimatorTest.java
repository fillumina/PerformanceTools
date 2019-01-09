package com.fillumina.performance.util;

import com.fillumina.performance.util.stats.Ratio;
import com.fillumina.performance.util.unit.IntervalUnit;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class LinearEtaEstimatorTest {

    private boolean printout;

    public static void main(final String[] args) {
        LinearEtaEstimatorTest test = new LinearEtaEstimatorTest();
        test.printout = true;
        test.shouldEstimateTheTimeOfArrival();
    }

    @Test
    public void shouldEstimateTheTimeOfArrival() {
        LinearEtaEstimator estimator = new LinearEtaEstimator();
        estimator.start();
        for (int i=0; i<20; i++) {
            final double eta = estimator.getEta(20-i)
                    .as(IntervalUnit.MILLISECONDS);
            final int sleepTime = 50;
            if (printout) {
                System.out.println("i=" + i +
                        "\t" + eta +
                        "\t" + ((20 - i) * sleepTime));
            }
            Sleeper.sleepMillis(sleepTime);
            if (i != 0) {
                ToleranceAssertion.assertEquals("different",
                        (20 - i) * sleepTime, eta, Ratio.percentage(50));
            }
        }
    }
}
