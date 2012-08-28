package com.fillumina.performance.interval;

import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author fra
 */
public class DoubleIntervalIteratorTest extends IntervalIteratorTestHelper {

    @Test
    public void shouldIterateOnDoubleFrom1To10() {
        final List<Double> list = iterate(DoubleIntervalIterator.cycleFor(),
                1D, 1.9D, 0.1D);

        assertEquals(10, list.size());
        assertEquals(1D, list.get(0), 1E-5);
        assertEquals(1.9, list.get(9), 1E-5);
    }
}
