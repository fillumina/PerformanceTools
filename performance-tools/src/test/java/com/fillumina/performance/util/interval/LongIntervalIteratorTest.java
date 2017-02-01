package com.fillumina.performance.util.interval;

import java.util.List;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati
 */
public class LongIntervalIteratorTest {

    @Test
    public void shouldIterateOnLongFrom1To10() {
        final List<Long> list = LongInterval.from(1L).to(10L).step(1L)
                .toList();

        assertEquals(9, list.size());
        assertEquals(1L, list.get(0), 0);
        assertEquals(9L, list.get(8), 0);
    }
}
