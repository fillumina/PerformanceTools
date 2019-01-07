package com.fillumina.performance.util.stats;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Use <a href="https://mycurvefit.com/">curvefit</a> to check the results.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class PowerFittingTest {

    public static class P implements PowerFitting.Point {
        private final double x,y;

        public P(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }
    }

    @Test
    public void shouldCreateRightParamenters() {
        P[] data = new P[5];
        data[0] = new P(1, 7);
        data[1] = new P(2, 6);
        data[2] = new P(3, 5.5);
        data[3] = new P(4, 5);
        data[4] = new P(5, 4.9);

        PowerFitting pf = new PowerFitting(data);
        double a = pf.getA();
        double b = pf.getB();

        assertEquals(7.011, a, 0.01);
        assertEquals(-0.229, b, 0.01);
    }

    @Test
    public void shouldGiveTheRightY() {
        P[] data = new P[5];
        data[0] = new P(1, 7);
        data[1] = new P(2, 6);
        data[2] = new P(3, 5.5);
        data[3] = new P(4, 5);
        data[4] = new P(5, 4.9);

        PowerFitting pf = new PowerFitting(data);

        assertEquals(6.29, pf.calculateXGivingY(4.6), 0.01);
        assertEquals(4.6, pf.calculateYGivingX(6.29), 0.01);
    }
}
