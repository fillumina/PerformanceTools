package com.fillumina.performance.util;

import java.util.Map;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
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

    @Test
    public void shouldTheReturnedMapBeAlwaysTheSame() {
        Bag<Integer> bag = new Bag<>();
        bag.add(1);
        bag.add(2);
        bag.add(1);
        bag.add(3);

        Map<Integer,Long> map1 = bag.getMap();
        Map<Integer,Long> map2 = bag.getMap();

        assertTrue(map1 == map2);
    }

    @Test
    public void shouldReturnAMap() {
        Bag<Integer> bag = new Bag<>();
        bag.add(1);
        bag.add(2);
        bag.add(1);
        bag.add(3);

        Map<Integer,Long> map = bag.getMap();

        assertEquals(2, map.get(1), 0);
        assertEquals(1, map.get(2), 0);
        assertEquals(1, map.get(3), 0);
    }
}
