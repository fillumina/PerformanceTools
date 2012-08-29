package com.fillumina.performance.util.interval;

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
        final List<Long> list = getList(
                LongInterval.cycleFor().start(1L).end(10L).step(1L));

        assertEquals(10, list.size());
        assertEquals(1L, list.get(0), 0);
        assertEquals(10L, list.get(9), 0);
    }
}
