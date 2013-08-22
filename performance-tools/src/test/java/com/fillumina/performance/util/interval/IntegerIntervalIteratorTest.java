package com.fillumina.performance.util.interval;

import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class IntegerIntervalIteratorTest {

    @Test
    public void shouldIterateOnInteger() {
        final List<Integer> list =
                IntegerInterval.cycle().from(1).to(10).step(1).toList();

        assertEquals(10, list.size());
        assertEquals(1, list.get(0), 0);
        assertEquals(10, list.get(9), 0);
    }
}
