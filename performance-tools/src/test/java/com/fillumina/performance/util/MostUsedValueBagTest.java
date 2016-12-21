package com.fillumina.performance.util;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MostUsedValueBagTest {

    @Test
    public void shouldAddAnElement() {
        MostUsedValueBag<String> bag = new MostUsedValueBag<>();
        bag.add("hello");
        assertEquals("hello", bag.getMostUsedValue());
    }

    @Test
    public void shouldReturnTheMostUsedOne() {
        MostUsedValueBag<String> bag = new MostUsedValueBag<>();
        bag.add("hello");
        bag.add("world");
        bag.add("hello");
        assertEquals("hello", bag.getMostUsedValue());
    }

    @Test
    public void shouldReturnTheMostUsedOne2() {
        MostUsedValueBag<String> bag = new MostUsedValueBag<>();
        bag.add("hello");
        bag.add("world");
        bag.add("hello");
        bag.add("world");
        bag.add("world");
        assertEquals("world", bag.getMostUsedValue());
    }

    @Test
    public void shouldResizeTheArray() {
        MostUsedValueBag<String> bag = new MostUsedValueBag<>(3);
        bag.add("1");
        bag.add("2");
        bag.add("3");
        bag.add("4");
        bag.add("5");
        bag.add("6");
        bag.add("4");
        assertEquals("4", bag.getMostUsedValue());
    }
}
