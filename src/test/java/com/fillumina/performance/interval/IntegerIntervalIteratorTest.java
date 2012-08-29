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
        final List<Integer> list = getList(
                IntegerIntervalIterator.cycleFor().start(1).end(10).step(1));

        assertEquals(10, list.size());
        assertEquals(1, list.get(0), 0);
        assertEquals(10, list.get(9), 0);
    }
}
