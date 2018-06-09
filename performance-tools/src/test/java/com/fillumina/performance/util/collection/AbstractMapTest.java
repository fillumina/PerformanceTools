package com.fillumina.performance.util.collection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class AbstractMapTest {

    protected abstract <K,V> Map<K,V> createMap();

    @Test(expected=IllegalStateException.class, timeout=300)
    public void shouldNotRemoveEmptyIterator() {
        Map<String,Integer> map = createMap();

        Iterator<Entry<String,Integer>> it = map.entrySet().iterator();

        it.remove();
    }

    @Test(timeout=300)
    public void shouldRemoveIteratingFirst() {
        final Map<String, Integer> map = populateMap();

        Iterator<Entry<String,Integer>> it = map.entrySet().iterator();

        while(! "one".equals(it.next().getKey()) ) {}

        it.remove();

        assertFalse(map.containsKey("one"));
    }

    @Test(timeout=300)
    public void shouldRemoveIteratingMiddle() {
        final Map<String, Integer> map = populateMap();

        Iterator<Entry<String,Integer>> it = map.entrySet().iterator();

        while(! "three".equals(it.next().getKey()) ) {}

        it.remove();

        assertFalse(map.containsKey("three"));
    }

    @Test(timeout=300)
    public void shouldRemoveIteratingLast() {
        final Map<String, Integer> map = populateMap();

        Iterator<Entry<String,Integer>> it = map.entrySet().iterator();

        while(! "five".equals(it.next().getKey()) ) {}

        it.remove();

        assertFalse(map.containsKey("five"));
    }

    @Test(expected=IllegalStateException.class, timeout=300)
    public void shouldNotRemoveTwice() {
        final Map<String, Integer> map = populateMap();

        Iterator<Entry<String,Integer>> it = map.entrySet().iterator();

        it.next();
        it.next();

        it.remove();
        it.remove(); // should throw exception
    }

    @Test(expected=IllegalStateException.class, timeout=300)
    public void shouldNotRemoveIfNextHasNotBeenCalled() {
        final Map<String, Integer> map = populateMap();

        Iterator<Entry<String,Integer>> it = map.entrySet().iterator();

        it.remove(); // should throw exception
    }

    @Test(timeout=300)
    public void shouldRemoveFirstAndOnlyElementInIterator() {
        Map<String,Integer> map = createMap();
        map.put("first", -1);

        Iterator<Entry<String,Integer>> it = map.entrySet().iterator();
        it.next();
        it.remove();

        assertTrue(map.isEmpty());
    }

    @Test(timeout=300)
    public void testIterator() {
        final Map<String, Integer> map = populateMap();
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

    @Test(timeout=300)
    public void shouldIterate() {
        Map<String,Integer> map = createMap();
        map.put("one", 1);
        map.put("two", 2);
        map.put("three", 3);
        map.put("four", 4);

        List<String> keys = new ArrayList<>(
                Arrays.asList("one", "two", "three", "four"));
        List<Integer> values = new ArrayList<>(Arrays.asList(1, 2, 3, 4));

        int index;
        Entry<String,Integer> entry;

        Iterator<Entry<String,Integer>> it = map.entrySet().iterator();

        assertTrue(it.hasNext());
        entry = it.next();
        index = keys.indexOf(entry.getKey());
        assertEquals(entry.getValue(), values.get(index), 0);
        keys.remove(index);
        values.remove(index);

        assertTrue(it.hasNext());
        entry = it.next();
        index = keys.indexOf(entry.getKey());
        assertEquals(entry.getValue(), values.get(index), 0);
        keys.remove(index);
        values.remove(index);

        assertTrue(it.hasNext());
        entry = it.next();
        index = keys.indexOf(entry.getKey());
        assertEquals(entry.getValue(), values.get(index), 0);
        keys.remove(index);
        values.remove(index);

        assertTrue(it.hasNext());
        entry = it.next();
        index = keys.indexOf(entry.getKey());
        assertEquals(entry.getValue(), values.get(index), 0);
        keys.remove(index);
        values.remove(index);

        assertFalse(it.hasNext());
        assertTrue(values.isEmpty());
        assertTrue(keys.isEmpty());
    }

    @Test(timeout=300)
    public void shouldRemoveWhileIterating() {
        Map<String,Integer> map = createMap();
        map.put("one", 1);
        map.put("two", 2);
        map.put("three", 3);
        map.put("four", 4);

        Iterator<Entry<String,Integer>> it = map.entrySet().iterator();

        assertTrue(it.hasNext());
        String removedKey = it.next().getKey();

        assertTrue(it.hasNext());

        it.remove();

        assertFalse(map.containsKey(removedKey));
    }

    @Test(timeout=300)
    public void shouldReturnTheValue() {
        Map<String,Integer> map = createMap();
        map.put("one", 1);
        assertEquals(1, map.get("one"), 0);
    }

    @Test(timeout=300)
    public void shouldClear() {
        Map<String,Integer> map = createMap();
        map.put("one", 1);
        map.clear();
        assertTrue(map.isEmpty());
        assertEquals(0, map.size());
    }

    @Test(timeout=300)
    public void shouldBeEmpty() {
        Map<String,Integer> map = createMap();
        assertTrue(map.isEmpty());
    }

    @Test(timeout=300)
    public void shouldNotBeEmpty() {
        Map<String,Integer> map = createMap();
        map.put("one", 1);
        assertFalse(map.isEmpty());
    }

    @Test(timeout=300)
    public void shouldReturnSize0() {
        Map<String,Integer> map = createMap();
        assertEquals(0, map.size());
    }

    @Test(timeout=300)
    public void shouldReturnSize2() {
        Map<String,Integer> map = createMap();
        map.put("one", 1);
        map.put("two", 2);
        assertEquals(2, map.size());
    }

    @Test(timeout=300)
    public void shouldReturnSize3() {
        Map<String,Integer> map = createMap();
        map.put("one", 1);
        map.put("two", 2);
        map.put("three", 2);
        assertEquals(3, map.size());
    }

    @Test(timeout=300)
    public void shouldReturnSize2IfRemovingElement() {
        Map<String,Integer> map = createMap();
        map.put("one", 1);
        map.put("two", 2);
        map.put("three", 2);
        map.remove("two");
        assertEquals(2, map.size());
    }

    @Test(timeout=300)
    public void shouldReturnSize0IfCleared() {
        Map<String,Integer> map = createMap();
        map.put("one", 1);
        map.put("two", 2);
        map.put("three", 2);
        map.clear();
        assertEquals(0, map.size());
    }

    @Test(timeout=300)
    public void shouldReturnEmptyIfCleared() {
        Map<String,Integer> map = createMap();
        assertTrue(map.isEmpty());

        map.put("one", 1);
        assertFalse(map.isEmpty());

        map.put("two", 2);
        assertFalse(map.isEmpty());

        map.put("three", 2);
        assertFalse(map.isEmpty());

        map.clear();
        assertTrue(map.isEmpty());
    }

    @Test(timeout=300)
    public void shouldOverwritePreviousEntryWithSameKey() {
        Map<String,Integer> map = createMap();
        map.put("one", 1);
        map.put("one", 2);
        assertEquals(1, map.size());
        assertEquals(2, map.get("one"), 0);
    }

    @Test(timeout=300)
    public void shouldReturnNullForANotExistentValueWhenEmpty() {
        Map<String,Integer> map = createMap();
        assertNull(map.get("one"));
    }

    @Test(timeout=300)
    public void shouldReturnNullForANotExistentValueWhenNotEmpty() {
        Map<String,Integer> map = populateMap();
        assertNull(map.get("not existent"));
    }

    @Test(timeout=300)
    public void shouldReturnSize1() {
        Map<String,Integer> map = createMap();
        map.put("one", 1);
        assertEquals(1, map.size());
    }

    @Test(timeout=300)
    public void shouldRemove() {
        Map<String,Integer> map = createMap();
        map.put("one", 1);
        map.remove("one");
        assertTrue(map.isEmpty());
    }

    @Test//(timeout=300)
    public void shouldRemoveFirst() {
        Map<String,Integer> map = createMap();
        map.put("one", 1);
        map.put("two", 2);
        map.put("three", 3);
        map.remove("one");
        assertEquals(2, map.size());
    }

    @Test(timeout=300)
    public void shouldRemoveMiddle() {
        Map<String,Integer> map = createMap();
        map.put("one", 1);
        map.put("two", 2);
        map.put("three", 3);
        map.remove("two");
        assertEquals(2, map.size());
    }

    @Test(timeout=300)
    public void shouldRemoveLast() {
        Map<String,Integer> map = createMap();
        map.put("one", 1);
        map.put("two", 2);
        map.put("three", 3);
        map.remove("three");
        assertEquals(2, map.size());
    }

    @Test(timeout=300)
    public void testPut() {
        Map<String,Integer> map = createMap();
        map.put("one", 1);
    }

    @Test(timeout=300)
    public void testIsEmpty() {
        Map<String,Integer> map = createMap();
        assertTrue(map.isEmpty());
    }

    @Test(timeout=300)
    public void testClear() {
        Map<String,Integer> map = createMap();
        assertTrue(map.isEmpty());
        map.put("hello", 1);
        assertFalse(map.isEmpty());
        map.clear();
        assertTrue(map.isEmpty());
    }

    @Test(timeout=300)
    public void testSize() {
        Map<String,Integer> map = createMap();
        map.put("one", 1);
        map.put("two", 1);
        map.put("three", 1);

        assertEquals(3, map.size());
    }

    @Test(timeout=300)
    public void shouldOverwriteKey() {
        Map<String,Integer> map = createMap();
        map.put("one", 1);
        map.put("two", 1);
        map.put("two", 1000);
        map.put("three", 1);

        assertEquals(3, map.size());
        assertEquals(1000, map.get("two"), 0);
    }

    @Test(timeout=300)
    public void shouldGet() {
        Map<String,Integer> map = createMap();
        map.put("one", 1);
        map.put("two", 2);
        assertEquals(2, map.get("two"), 0);
    }

    @Test(timeout=300)
    public void shouldRemoveNonExistentKey() {
        Map<String,Integer> map = createMap();
        map.put("one", 1);
        map.put("two", 2);
        assertEquals(2, map.get("two"), 0);
        map.remove("non existent");
        assertEquals(2, map.size(), 0);
    }

    @Test(timeout=300)
    public void shouldContainsKey() {
        Map<String,Integer> map = populateMap();
        assertEquals(5, map.size(), 0);
        assertTrue(map.containsKey("one"));
        assertTrue(map.containsKey("two"));
        assertTrue(map.containsKey("three"));
        assertTrue(map.containsKey("four"));
        assertTrue(map.containsKey("five"));
    }

    @Test(timeout=300)
    public void shouldContainsValue() {
        Map<String,Integer> map = populateMap();
        assertEquals(5, map.size(), 0);
        assertTrue(map.containsValue(1));
        assertTrue(map.containsValue(2));
        assertTrue(map.containsValue(3));
        assertTrue(map.containsValue(4));
        assertTrue(map.containsValue(5));
    }

    @Test(timeout=300)
    public void shouldPutAllInAnEmptyMap() {
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

    @Test(timeout=300)
    public void shouldPutAllInANotEmptyMap() {
        Map<String,Integer> copy = new HashMap<>();
        copy.put("one", 1);
        copy.put("two", 2);
        copy.put("three", 3);
        copy.put("four", 4);
        copy.put("five", 5);

        Map<String,Integer> map = createMap();
        map.put("alpha", 65);
        map.put("beta", 66);
        map.putAll(copy);

        assertEquals(65, map.get("alpha"), 0);
        assertEquals(66, map.get("beta"), 0);
        assertEquals(1, map.get("one"), 0);
        assertEquals(2, map.get("two"), 0);
        assertEquals(3, map.get("three"), 0);
        assertEquals(4, map.get("four"), 0);
        assertEquals(5, map.get("five"), 0);
    }

    @Test(timeout=300)
    public void shouldHashCodeBeEquals() {
        Map<String,Integer> map1 = populateMap();
        Map<String,Integer> map2 = populateMap();

        assertEquals(map1.hashCode(), map2.hashCode(), 0);
    }

    @Test(timeout=300)
    public void shouldBeEquals() {
        Map<String,Integer> map1 = populateMap();
        Map<String,Integer> map2 = populateMap();

        assertTrue(map1.equals(map2));
        assertTrue(map2.equals(map1));
        assertTrue(map1.equals(map1));
    }

    public static class SameHash {
        private final int value;

        public SameHash(int value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "SameHash{" + "value=" + value + '}';
        }

        @Override
        public int hashCode() {
            return 123;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            return this.value == ((SameHash) obj).value;
        }
    }

    @Test(timeout=300)
    public void shouldPutElementsWithSameHash() {
        SameHash a = new SameHash(1);
        SameHash b = new SameHash(2);
        SameHash c = new SameHash(3);

        assertNotEquals(a, b);
        assertNotEquals(c, b);
        assertNotEquals(a, c);
        assertEquals(a.hashCode(), b.hashCode(), 0);
        assertEquals(b.hashCode(), c.hashCode(), 0);

        Map<SameHash,Integer> map = createMap();
        map.put(a, 1);
        map.put(b, 2);
        map.put(c, 3);

        assertEquals(3, map.size(), 0);

        assertEquals(1, map.get(a), 0);
        assertEquals(2, map.get(b), 0);
        assertEquals(3, map.get(c), 0);

        assertTrue(map.containsKey(a));
        assertTrue(map.containsKey(b));
        assertTrue(map.containsKey(c));

        map.remove(b);
        assertEquals(1, map.get(a), 0);
        assertNull(map.get(b));
        assertEquals(3, map.get(c), 0);
    }

    @Test(timeout=300)
    public void testKeySet() {
        final Map<String, Integer> map = populateMap();
        Set<String> set = map.keySet();
        assertEquals(5, set.size(), 0);
        assertTrue(set.contains("one"));
        assertTrue(set.contains("two"));
        assertTrue(set.contains("three"));
        assertTrue(set.contains("four"));
        assertTrue(set.contains("five"));
    }

    @Test(timeout=300)
    public void testValues() {
        final Map<String, Integer> map = populateMap();
        Collection<Integer> coll = map.values();
        assertEquals(5, coll.size(), 0);
        assertTrue(coll.contains(1));
        assertTrue(coll.contains(2));
        assertTrue(coll.contains(3));
        assertTrue(coll.contains(4));
        assertTrue(coll.contains(5));
    }

    @Test(timeout=300)
    public void testEntrySet() {
        final Map<String, Integer> map = populateMap();
        Set<Entry<String,Integer>> set = map.entrySet();

        Set<String> keys = new HashSet<>();
        for (Entry<String,Integer> e : set) {
            //System.out.println("key=" + e.getKey());
            keys.add(e.getKey());
        }
        assertTrue(keys.contains("one"));
        assertTrue(keys.contains("two"));
        assertTrue(keys.contains("three"));
        assertTrue(keys.contains("four"));
        assertTrue(keys.contains("five"));
        assertEquals(5, keys.size(), 0);

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

    protected Map<String,Integer> populateMap() {
        Map<String,Integer> map = createMap();
        map.put("one", 1);
        map.put("two", 2);
        map.put("three", 3);
        map.put("four", 4);
        map.put("five", 5);
        return map;
    }

    @Test
    public void shouldUseEntrySetForEach() {
        Map<String,Integer> map = populateMap();

        LinkedHashMap<String,Integer> m = new LinkedHashMap<>();
        map.entrySet().forEach(e -> m.put(e.getKey(), e.getValue()));

        assertEqualMap(map, m);
    }

    @Test
    public void shouldUseEntrySetStream() {
        Map<String,Integer> map = populateMap();

        LinkedHashMap<String,Integer> m = new LinkedHashMap<>();
        map.entrySet().stream().forEach(e -> m.put(e.getKey(), e.getValue()));

        assertEqualMap(map, m);
    }

    private void assertEqualMap(Map<String,Integer> m1, Map<String,Integer> m2) {
        assertEquals(m1.size(), m2.size(), 0);

        Iterator<String> keyIterator = m1.keySet().iterator();
        while (keyIterator.hasNext()) {
            final String key = keyIterator.next();
            assertTrue(m2.containsKey(key));
            assertEquals(m1.get(key), m2.get(key));
        }
    }

    @Test
    public void shouldEntriesBeDistinctInStream() {
        final Map<String, Integer> map = populateMap();

        Optional<Entry<String,Integer>> result =
                map.entrySet().stream().max((e1, e2) ->
                        Integer.compare(e1.getValue(), e2.getValue()));

        assertTrue(result.isPresent());
        Entry<String,Integer> entry = result.get();
        assertEquals("five", entry.getKey());
        assertEquals(5, entry.getValue(), 0);
    }

    @Test
    public void shouldAnEntryBeValidEvenIfMapIsModified() {
        final Map<String, Integer> map = populateMap();
        Entry<String,Integer> entry = map.entrySet().iterator().next();

        assertNotNull(entry);
        String key = entry.getKey();
        int value = entry.getValue();

        map.clear();

        assertEquals(key, entry.getKey());
        assertEquals(value, entry.getValue(), 0);
    }

    @Test
    public void shouldAnEntryUseTheLastValidValueIfMapIsCleared() {
        final Map<String, Integer> map = populateMap();
        Entry<String,Integer> entry = map.entrySet().iterator().next();

        assertNotNull(entry);
        String key = entry.getKey();
        int value = entry.getValue();

        map.clear();

        map.put(key, value + 1);
        assertEquals(key, entry.getKey());
        assertEquals(value, entry.getValue(), 0);
    }

    @Test
    public void shouldAnEntryUpdateItsValueIfItChanges() {
        final Map<String, Integer> map = populateMap();
        Entry<String,Integer> entry = map.entrySet().iterator().next();

        assertNotNull(entry);
        String key = entry.getKey();
        int value = entry.getValue();
        final int newValue = value + 1;

        map.put(key, newValue);

        assertEquals(key, entry.getKey());
        assertEquals(newValue, entry.getValue(), 0);
    }
}
