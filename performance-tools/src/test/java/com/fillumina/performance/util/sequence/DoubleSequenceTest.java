package com.fillumina.performance.util.sequence;

import java.util.List;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati
 */
public class DoubleSequenceTest {

    @Test
    public void shouldIterateOnDoubleFrom1To2() {
        final List<Double> list =
                DoubleSequence.from(1D).to(1.9D).step(0.1D).toList();

        assertEquals(10, list.size());
        assertEquals(1D, list.get(0), 1E-5);
        assertEquals(1.8D, list.get(8), 1E-5);
    }

    @Test
    public void shouldIterateOnDoubleFromMinus1To1() {
        final List<Double> list =
                DoubleSequence.from(-1D).to(1D).step(0.1D).toList();

        assertEquals(21, list.size());
        assertEquals(-1D, list.get(0), 1E-5);
        assertEquals(0, list.get(10), 1E-5);
        assertEquals(1, list.get(20), 1E-5);
    }
}
