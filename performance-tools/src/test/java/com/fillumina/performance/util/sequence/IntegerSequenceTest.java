package com.fillumina.performance.util.sequence;

import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati
 */
public class IntegerSequenceTest {

    @Test
    public void shouldIterateOnInteger() {
        final List<Integer> list =
                IntegerSequence.from(0).to(10).step(1).toList();

        assertEquals(11, list.size());
        for (int i=0; i<10; i++) {
            assertEquals(i, list.get(i), 0);
        }
    }

    @Test
    public void shouldIterateOnIntegerStep() {
        final List<Integer> list =
                IntegerSequence.from(0).to(10).step(3).toList();

        assertEquals(4, list.size());
        for (int i=0; i<4; i++) {
            assertEquals(i * 3, list.get(i), 0);
        }
    }

    @Test
    public void shouldIterateOnIntegerStepWithFirstDifferentThan0() {
        final List<Integer> list =
                IntegerSequence.from(2).to(20).step(2).toList();

        assertEquals(10, list.size());
        for (int i=0; i<9; i++) {
            assertEquals((i + 1) * 2, list.get(i), 0);
        }
    }

    @Test
    public void shouldBeReusable() {
        Iterable<Integer> interval = IntegerSequence.from(0).to(20).step(5);

        List<Integer> list1 = new ArrayList<>();
        for (int i : interval) {
            list1.add(i);
        }

        List<Integer> list2 = new ArrayList<>();
        for (int i : interval) {
            list2.add(i);
        }

        assertEquals(list1, list2);
    }
}
