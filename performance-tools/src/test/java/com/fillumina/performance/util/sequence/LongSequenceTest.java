package com.fillumina.performance.util.sequence;

import java.util.List;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati
 */
public class LongSequenceTest {

    @Test
    public void shouldIterateOnLongFrom1To10() {
        final List<Long> list = LongSequence.from(1L).to(10L).step(1L).toList();

        assertEquals(10, list.size());
        assertEquals(1L, list.get(0), 0);
        assertEquals(9L, list.get(8), 0);
    }
}
