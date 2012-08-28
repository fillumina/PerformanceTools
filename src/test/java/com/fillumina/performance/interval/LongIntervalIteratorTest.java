package com.fillumina.performance.interval;

import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author fra
 */
public class LongIntervalIteratorTest extends IntervalIteratorTestHelper {

    @Test
    public void shouldIterateOnLongFrom1To10() {
        final List<Long> list = iterate(LongIntervalIterator.cycleFor(),
                1L, 10L, 1L);

        assertEquals(10, list.size());
        assertEquals(1L, list.get(0), 0);
        assertEquals(10L, list.get(9), 0);
    }
}
