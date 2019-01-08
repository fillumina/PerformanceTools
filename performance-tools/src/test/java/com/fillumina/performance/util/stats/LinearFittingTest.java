package com.fillumina.performance.util.stats;

import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * Using example found at:
 * https://www.varsitytutors.com/hotmath/hotmath_help/topics/line-of-best-fit
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class LinearFittingTest {

    @Test
    public void shouldGiveCorrectResult() {
        List<Point> list = new ArrayList<>();
        list.add(new PointImpl(8, 3));
        list.add(new PointImpl(2, 10));
        list.add(new PointImpl(11, 3));
        list.add(new PointImpl(6, 6));
        list.add(new PointImpl(5, 8));
        list.add(new PointImpl(4, 12));
        list.add(new PointImpl(12, 1));
        list.add(new PointImpl(9, 4));
        list.add(new PointImpl(6, 9));
        list.add(new PointImpl(1, 14));

        LinearFitting lf = new LinearFitting(list);

        assertEquals(-1.1, lf.getM(), 0.1);
        assertEquals(14.0, lf.getB(), 0.1);

    }

}
