package com.fillumina.performance.util.collection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ArrayMapTest extends AbstractMapTest {

    @Override
    protected <K, V> Map<K, V> createMap() {
        return new ArrayMap<>();
    }

    @Test
    public void shouldGetTheEntryAtIndex() {
        ArrayMap<String,Integer> map = new ArrayMap<String,Integer>()
                .add("one", 1)
                .add("two", 2)
                .add("three", 3)
                .add("four", 4);

        Map.Entry<String,Integer> e = map.getEntryAtIndex(2);

        assertEquals("three", e.getKey());
        assertEquals(3, e.getValue(), 0);
    }

    @Test
    public void shouldGetTheKeyAtIndex() {
        ArrayMap<String,Integer> map = new ArrayMap<String,Integer>()
                .add("one", 1)
                .add("two", 2)
                .add("three", 3)
                .add("four", 4);

        assertEquals("two", map.getKeyAtIndex(1));
    }

    @Test
    public void shouldGetTheValueAtIndex() {
        ArrayMap<String,Integer> map = new ArrayMap<String,Integer>()
                .add("one", 1)
                .add("two", 2)
                .add("three", 3)
                .add("four", 4);

        assertEquals(4, map.getValueAtIndex(3), 0);
    }


    @Test
    public void shouldDetectUnmodifiableMapWhileNotModifyingTheMap() {
        ArrayMap<String,Integer> map = new ArrayMap<String,Integer>()
                .add("one", 1)
                .add("two", 2)
                .add("three", 3)
                .add("four", 4);

        List<Entry<String,Integer>> copyBefore = new ArrayList<>(map.entrySet());

        assertTrue(map.unmodifiable().isUnmodifiable());

        List<Entry<String,Integer>> copyAfter = new ArrayList<>(map.entrySet());

        assertEquals(copyBefore, copyAfter);
    }
}
