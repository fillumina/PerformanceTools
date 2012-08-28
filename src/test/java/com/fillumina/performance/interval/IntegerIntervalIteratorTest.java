package com.fillumina.performance.interval;

import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author fra
 */
public class IntegerIntervalIteratorTest extends IntervalIteratorTestHelper {

    @Test
    public void shouldIterateOnInteger() {
        final List<Integer> list = iterate(IntegerIntervalIterator.cycleFor(),
                1, 10, 1);

        assertEquals(10, list.size());
        assertEquals(1L, list.get(0), 0);
        assertEquals(10, list.get(9), 0);
    }
}
