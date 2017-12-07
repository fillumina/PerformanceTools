package com.fillumina.performance.util.collection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
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
    public void shouldUseCopyConstructor() {
        Map<Integer,String> map = new LinkedHashMap<>();
        map.put(1, "one");
        map.put(2, "two");
        map.put(3, "three");
        map.put(4, "four");

        Map<Integer,String> copy = new ArrayMap<>(map);
        assertEquals("one", copy.get(1));
        assertEquals("two", copy.get(2));
        assertEquals("three", copy.get(3));
        assertEquals("four", copy.get(4));
    }

    @Test
    public void shouldUseCloneConstructor() {
        ArrayMap<Integer,String> map = new ArrayMap<>();
        map.put(1, "one");
        map.put(2, "two");
        map.put(3, "three");
        map.put(4, "four");

        Map<Integer,String> copy = new ArrayMap<>(map);
        assertEquals("one", copy.get(1));
        assertEquals("two", copy.get(2));
        assertEquals("three", copy.get(3));
        assertEquals("four", copy.get(4));
    }

    @Test
    public void shouldIterateThroughAllEntries() {
        ArrayMap<Integer,String> map = new ArrayMap<>();
        map.put(1, "one");
        map.put(2, "two");
        map.put(3, "three");
        map.put(4, "four");

        List<Integer> keys = new ArrayList<>();
        List<String> values = new ArrayList<>();
        map.forEach((k,v) -> { keys.add(k); values.add(v); } );

        assertEquals(Arrays.asList(1, 2, 3, 4), keys);
        assertEquals(Arrays.asList("one", "two", "three", "four"), values);
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
