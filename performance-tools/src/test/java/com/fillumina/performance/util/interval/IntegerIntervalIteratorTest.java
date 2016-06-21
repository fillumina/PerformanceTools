package com.fillumina.performance.util.interval;

import java.util.List;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati
 */
public class IntegerIntervalIteratorTest {

    @Test
    public void shouldIterateOnInteger() {
        final List<Integer> list =
                IntegerInterval.from(1).to(10).step(1).toList();

        assertEquals(10, list.size());
        assertEquals(1, list.get(0), 0);
        assertEquals(10, list.get(9), 0);
    }
}
