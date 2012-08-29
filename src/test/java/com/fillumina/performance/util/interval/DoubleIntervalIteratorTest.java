package com.fillumina.performance.util.interval;

import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author fra
 */
public class DoubleIntervalIteratorTest extends IntervalIteratorTestHelper {

    @Test
    public void shouldIterateOnDoubleFrom1To2() {
        final List<Double> list = getList(
                DoubleInterval.cycleFor().start(1D).end(1.9D).step(0.1D));

        assertEquals(10, list.size());
        assertEquals(1D, list.get(0), 1E-5);
        assertEquals(1.9D, list.get(9), 1E-5);
    }

    @Test
    public void shouldIterateOnDoubleFromMinus1To1() {
        final List<Double> list = getList(
                DoubleInterval.cycleFor().start(-1D).end(1D).step(0.1D));

        assertEquals(21, list.size());
        assertEquals(-1D, list.get(0), 1E-5);
        assertEquals(0, list.get(10), 1E-5);
        assertEquals(1, list.get(20), 1E-5);
    }
}
