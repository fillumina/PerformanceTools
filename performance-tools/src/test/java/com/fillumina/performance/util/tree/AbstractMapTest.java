package com.fillumina.performance.util.tree;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class AbstractMapTest {

    protected abstract <K,V> Map<K,V> createMap();

    @Test
    public void testPut() {
        Map<String,Integer> map = createMap();
        map.put("one", 1);
    }

    @Test
    public void testIsEmpty() {
        Map<String,Integer> map = createMap();
        assertTrue(map.isEmpty());
    }

    @Test
    public void testClear() {
        Map<String,Integer> map = createMap();
        assertTrue(map.isEmpty());
        map.put("hello", 1);
        assertFalse(map.isEmpty());
        map.clear();
        assertTrue(map.isEmpty());
    }

    @Test
    public void testSize() {
        Map<String,Integer> map = createMap();
        map.put("one", 1);
        map.put("two", 1);
        map.put("three", 1);

        assertEquals(3, map.size());
    }

    @Test
    public void shouldOverwriteKey() {
        Map<String,Integer> map = createMap();
        map.put("one", 1);
        map.put("two", 1);
        map.put("two", 1000);
        map.put("three", 1);

        assertEquals(3, map.size());
        assertEquals(1000, map.get("two"), 0);
    }

    @Test
    public void testGet() {
        Map<String,Integer> map = createMap();
        map.put("one", 1);
        map.put("two", 2);
        assertEquals(2, map.get("two"), 0);
    }

    @Test
    public void testRemove() {
        Map<String,Integer> map = createMap();
        map.put("one", 1);
        map.put("two", 2);
        assertEquals(2, map.get("two"), 0);
        map.remove("two");
        assertNull(map.get("two"));
    }

    @Test
    public void shouldRemoveNonExistentKey() {
        Map<String,Integer> map = createMap();
        map.put("one", 1);
        map.put("two", 2);
        assertEquals(2, map.get("two"), 0);
        map.remove("non existent");
        assertEquals(2, map.size(), 0);
    }

    @Test
    public void testContainsKey() {
        Map<String,Integer> map = popolateMap();
        assertEquals(5, map.size(), 0);
        assertTrue(map.containsKey("one"));
        assertTrue(map.containsKey("two"));
        assertTrue(map.containsKey("three"));
        assertTrue(map.containsKey("four"));
        assertTrue(map.containsKey("five"));
    }

    @Test
    public void testContainsValue() {
        Map<String,Integer> map = popolateMap();
        assertEquals(5, map.size(), 0);
        assertTrue(map.containsValue(1));
        assertTrue(map.containsValue(2));
        assertTrue(map.containsValue(3));
        assertTrue(map.containsValue(4));
        assertTrue(map.containsValue(5));
    }

    @Test
    public void testPutAll() {
        Map<String,Integer> copy = new HashMap<>();
        copy.put("one", 1);
        copy.put("two", 2);
        copy.put("three", 3);
        copy.put("four", 4);
        copy.put("five", 5);

        Map<String,Integer> map = createMap();
        map.putAll(copy);

        assertEquals(1, map.get("one"), 0);
        assertEquals(2, map.get("two"), 0);
        assertEquals(3, map.get("three"), 0);
        assertEquals(4, map.get("four"), 0);
        assertEquals(5, map.get("five"), 0);
    }

    @Test
    public void shouldHashCodeBeEquals() {
        Map<String,Integer> map1 = createMap();
        Map<String,Integer> map2 = createMap();

        assertEquals(map1.hashCode(), map2.hashCode(), 0);
    }

    @Test
    public void shouldBeEquals() {
        Map<String,Integer> map1 = createMap();
        Map<String,Integer> map2 = createMap();

        assertTrue(map1.equals(map2));
        assertTrue(map2.equals(map1));
        assertTrue(map1.equals(map1));
    }

    @Test
    public void testKeySet() {
        final Map<String, Integer> map = popolateMap();
        Set<String> set = map.keySet();
        assertEquals(5, set.size(), 0);
        assertTrue(set.contains("one"));
        assertTrue(set.contains("two"));
        assertTrue(set.contains("three"));
        assertTrue(set.contains("four"));
        assertTrue(set.contains("five"));
    }

    @Test
    public void testValues() {
        final Map<String, Integer> map = popolateMap();
        Collection<Integer> coll = map.values();
        assertEquals(5, coll.size(), 0);
        assertTrue(coll.contains(1));
        assertTrue(coll.contains(2));
        assertTrue(coll.contains(3));
        assertTrue(coll.contains(4));
        assertTrue(coll.contains(5));
    }

    @Test
    public void testEntrySet() {
        final Map<String, Integer> map = popolateMap();
        Set<Entry<String,Integer>> set = map.entrySet();

        Set<String> keys = new HashSet<>();
        for (Entry<String,Integer> e : set) {
            keys.add(e.getKey());
        }
        assertEquals(5, keys.size(), 0);
        assertTrue(keys.contains("one"));
        assertTrue(keys.contains("two"));
        assertTrue(keys.contains("three"));
        assertTrue(keys.contains("four"));
        assertTrue(keys.contains("five"));

        Set<Integer> values = new HashSet<>();
        for (Entry<String,Integer> e : set) {
            values.add(e.getValue());
        }
        assertEquals(5, values.size(), 0);
        assertTrue(values.contains(1));
        assertTrue(values.contains(2));
        assertTrue(values.contains(3));
        assertTrue(values.contains(4));
        assertTrue(values.contains(5));
    }

    protected Map<String,Integer> popolateMap() {
        Map<String,Integer> map = createMap();
        map.put("one", 1);
        map.put("two", 2);
        map.put("three", 3);
        map.put("four", 4);
        map.put("five", 5);
        return map;
    }
}
