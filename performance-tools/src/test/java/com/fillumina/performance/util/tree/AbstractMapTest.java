package com.fillumina.performance.util.tree;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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

    @Test(expected=IllegalStateException.class)
    public void shouldNotRemoveEmptyIterator() {
        Map<String,Integer> map = createMap();

        Iterator<Entry<String,Integer>> it = map.entrySet().iterator();

        it.remove();
    }

    @Test
    public void shouldRemoveIteratingFirst() {
        final Map<String, Integer> map = popolateMap();

        Iterator<Entry<String,Integer>> it = map.entrySet().iterator();

        assertEquals("one", it.next().getKey());

        it.remove();

        assertFalse(map.containsKey("one"));

        assertEquals("two", it.next().getKey());
    }

    @Test
    public void shouldRemoveIteratingMiddle() {
        final Map<String, Integer> map = popolateMap();

        Iterator<Entry<String,Integer>> it = map.entrySet().iterator();

        assertEquals("one", it.next().getKey());
        assertEquals("two", it.next().getKey());

        it.remove();

        assertFalse(map.containsKey("two"));

        assertEquals("three", it.next().getKey());
        it.remove();

        assertFalse(map.containsKey("three"));
    }

    @Test
    public void shouldRemoveIteratingLast() {
        final Map<String, Integer> map = popolateMap();

        Iterator<Entry<String,Integer>> it = map.entrySet().iterator();

        assertEquals("one", it.next().getKey());
        assertEquals("two", it.next().getKey());
        assertEquals("three", it.next().getKey());
        assertEquals("four", it.next().getKey());
        assertEquals("five", it.next().getKey());

        it.remove();

        assertFalse(map.containsKey("five"));

        assertEquals(4, map.size(), 0);
    }

    @Test(expected=IllegalStateException.class)
    public void shouldNotRemoveTwice() {
        final Map<String, Integer> map = popolateMap();

        Iterator<Entry<String,Integer>> it = map.entrySet().iterator();

        assertEquals("one", it.next().getKey());
        assertEquals("two", it.next().getKey());

        it.remove();
        it.remove(); // should throw exception
    }

    @Test(expected=IllegalStateException.class)
    public void shouldNotRemoveIfNextHasNotBeenCalled() {
        final Map<String, Integer> map = popolateMap();

        Iterator<Entry<String,Integer>> it = map.entrySet().iterator();

        it.remove(); // should throw exception
    }

    @Test
    public void shouldRemoveFirstAndOnlyElementInIterator() {
        Map<String,Integer> map = createMap();
        map.put("first", -1);

        Iterator<Entry<String,Integer>> it = map.entrySet().iterator();
        it.next();
        it.remove();

        assertTrue(map.isEmpty());
    }

    @Test
    public void testIterator() {
        final Map<String, Integer> map = popolateMap();
        Iterator<Entry<String,Integer>> it = map.entrySet().iterator();

        Set<String> set = new HashSet<>();
        while (it.hasNext()) {
            set.add(it.next().getKey());
        }

        assertEquals(5, set.size(), 0);
        assertTrue(set.contains("one"));
        assertTrue(set.contains("two"));
        assertTrue(set.contains("three"));
        assertTrue(set.contains("four"));
        assertTrue(set.contains("five"));
    }

    @Test
    public void shouldIterate() {
        Map<String,Integer> map = createMap();
        map.put("one", 1);
        map.put("two", 2);
        map.put("three", 3);
        map.put("four", 4);

        Iterator<Entry<String,Integer>> it = map.entrySet().iterator();

        assertTrue(it.hasNext());
        Entry<String,Integer> entry = it.next();
        assertEquals("one", entry.getKey());
        assertEquals(1, entry.getValue(), 0);

        assertTrue(it.hasNext());
        entry = it.next();
        assertEquals("two", entry.getKey());
        assertEquals(2, entry.getValue(), 0);

        assertTrue(it.hasNext());
        entry = it.next();
        assertEquals("three", entry.getKey());
        assertEquals(3, entry.getValue(), 0);

        assertTrue(it.hasNext());
        entry = it.next();
        assertEquals("four", entry.getKey());
        assertEquals(4, entry.getValue(), 0);

        assertFalse(it.hasNext());
    }

    @Test
    public void shouldRemoveWhileIterating() {
        Map<String,Integer> map = createMap();
        map.put("one", 1);
        map.put("two", 2);
        map.put("three", 3);
        map.put("four", 4);

        Iterator<Entry<String,Integer>> it = map.entrySet().iterator();

        assertTrue(it.hasNext());
        Entry<String,Integer> entry = it.next();
        assertEquals("one", entry.getKey());
        assertEquals(1, entry.getValue(), 0);

        assertTrue(it.hasNext());

        it.remove();

        assertFalse(map.containsKey("one"));
    }

    @Test
    public void shouldRetunrTheValue() {
        Map<String,Integer> map = createMap();
        map.put("one", 1);
        assertEquals(1, map.get("one"), 0);
    }

    @Test
    public void shouldClear() {
        Map<String,Integer> map = createMap();
        map.put("one", 1);
        map.clear();
        assertTrue(map.isEmpty());
    }

    @Test
    public void shouldBeEmpty() {
        Map<String,Integer> map = createMap();
        assertTrue(map.isEmpty());
    }

    @Test
    public void testIsNotEmpty() {
        Map<String,Integer> map = createMap();
        map.put("one", 1);
        assertFalse(map.isEmpty());
    }

    @Test
    public void shouldReturnSize0() {
        Map<String,Integer> map = createMap();
        assertEquals(0, map.size());
    }

    @Test
    public void shouldReturnSize2() {
        Map<String,Integer> map = createMap();
        map.put("one", 1);
        map.put("two", 2);
        assertEquals(2, map.size());
    }

    @Test
    public void shouldOverwritePreviousEntryWithSameKey() {
        Map<String,Integer> map = createMap();
        map.put("one", 1);
        map.put("one", 2);
        assertEquals(1, map.size());
        assertEquals(2, map.get("one"), 0);
    }

    @Test
    public void shouldReturnNullForANotExistentValue() {
        Map<String,Integer> map = createMap();
        assertNull(map.get("one"));
    }

    @Test
    public void shouldReturnSize1() {
        Map<String,Integer> map = createMap();
        map.put("one", 1);
        assertEquals(1, map.size());
    }

    @Test
    public void shouldRemove() {
        Map<String,Integer> map = createMap();
        map.put("one", 1);
        map.remove("one");
        assertTrue(map.isEmpty());
    }

    @Test
    public void shouldRemoveFirst() {
        Map<String,Integer> map = createMap();
        map.put("one", 1);
        map.put("two", 2);
        map.put("three", 3);
        map.remove("one");
        assertEquals(2, map.size());
    }

    @Test
    public void shouldRemoveMiddle() {
        Map<String,Integer> map = createMap();
        map.put("one", 1);
        map.put("two", 2);
        map.put("three", 3);
        map.remove("two");
        assertEquals(2, map.size());
    }

    @Test
    public void shouldRemoveLast() {
        Map<String,Integer> map = createMap();
        map.put("one", 1);
        map.put("two", 2);
        map.put("three", 3);
        map.remove("three");
        assertEquals(2, map.size());
    }

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
    public void shouldGet() {
        Map<String,Integer> map = createMap();
        map.put("one", 1);
        map.put("two", 2);
        assertEquals(2, map.get("two"), 0);
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
    public void shouldContainsKey() {
        Map<String,Integer> map = popolateMap();
        assertEquals(5, map.size(), 0);
        assertTrue(map.containsKey("one"));
        assertTrue(map.containsKey("two"));
        assertTrue(map.containsKey("three"));
        assertTrue(map.containsKey("four"));
        assertTrue(map.containsKey("five"));
    }

    @Test
    public void shouldContainsValue() {
        Map<String,Integer> map = popolateMap();
        assertEquals(5, map.size(), 0);
        assertTrue(map.containsValue(1));
        assertTrue(map.containsValue(2));
        assertTrue(map.containsValue(3));
        assertTrue(map.containsValue(4));
        assertTrue(map.containsValue(5));
    }

    @Test
    public void shouldPutAll() {
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
