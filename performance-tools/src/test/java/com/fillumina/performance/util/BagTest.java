package com.fillumina.performance.util;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class BagTest {

    @Test
    public void shouldReturnFrequencies() {
        Bag<Integer> bag = new Bag<>();
        bag.add(1);
        bag.add(2);
        bag.add(1);
        bag.add(3);
        bag.add(2);
        bag.add(1);
        assertEquals(1, bag.getCount(3));
        assertEquals(2, bag.getCount(2));
        assertEquals(3, bag.getCount(1));
    }

    @Test
    public void shouldReturnTheMostFrequentOneFirst() {
        Bag<Integer> bag = new Bag<>();
        bag.add(1);
        bag.add(2);
        bag.add(1);
        bag.add(3);
        assertEquals(1, bag.getOrderedEntryList().get(0).getValue(), 0);
    }
}
