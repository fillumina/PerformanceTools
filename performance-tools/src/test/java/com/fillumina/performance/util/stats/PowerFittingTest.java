package com.fillumina.performance.util.stats;

import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Use <a href="https://mycurvefit.com/">curvefit</a> to check the results.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class PowerFittingTest {


    @Test
    public void shouldCreateRightParamenters() {
        List<PointImpl> data = new ArrayList<>(5);
        data.add(new PointImpl(1, 7));
        data.add(new PointImpl(2, 6));
        data.add(new PointImpl(3, 5.5));
        data.add(new PointImpl(4, 5));
        data.add(new PointImpl(5, 4.9));

        PowerFitting pf = new PowerFitting(data);
        double a = pf.getA();
        double b = pf.getB();

        assertEquals(7.011, a, 0.01);
        assertEquals(-0.229, b, 0.01);
    }

    @Test
    public void shouldGiveTheRightY() {
        List<PointImpl> data = new ArrayList<>(5);
        data.add(new PointImpl(1, 7));
        data.add(new PointImpl(2, 6));
        data.add(new PointImpl(3, 5.5));
        data.add(new PointImpl(4, 5));
        data.add(new PointImpl(5, 4.9));

        PowerFitting pf = new PowerFitting(data);

        assertEquals(6.29, pf.calculateXGivingY(4.6), 0.01);
        assertEquals(4.6, pf.calculateYGivingX(6.29), 0.01);
    }
}
