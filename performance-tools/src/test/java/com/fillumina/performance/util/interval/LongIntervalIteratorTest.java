package com.fillumina.performance.util.interval;

import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Francesco Illuminati
 */
public class LongIntervalIteratorTest {

    @Test
    public void shouldIterateOnLongFrom1To10() {
        final List<Long> list = LongInterval.cycle().from(1L).to(10L).step(1L)
                .toList();

        assertEquals(10, list.size());
        assertEquals(1L, list.get(0), 0);
        assertEquals(10L, list.get(9), 0);
    }
}
