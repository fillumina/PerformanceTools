package com.fillumina.performance.util.collection;

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ArrayListMapTest extends AbstractMapTest {

    private static class ArrayMapImpl<K,V> implements Map<K,V> {
        private final ArrayListMap<K,Entry<K,V>> map =
                new ArrayListMap<>(e -> e.getKey());

        @Override
        public int size() {
            return map.size();
        }

        @Override
        public boolean isEmpty() {
            return map.isEmpty();
        }

        @Override
        public boolean containsKey(Object key) {
            return map.containsKey(key);
        }

        @Override
        public boolean containsValue(Object value) {
            for (Entry<K,Entry<K,V>> e : map) {
                Entry<K,V> entry = e.getValue();
                if (Objects.equals(value, entry.getValue())) {
                    return map.containsValue(entry);
                }
            }
            return map.containsValue(value);
        }

        @Override
        public V get(Object key) {
            Entry<K, V> entry = map.get(key);
            return entry == null ? null : entry.getValue();
        }

        @Override
        public V put(K key, V value) {
            Entry<K, V> entry = map.put(key, new MapEntry<>(key, value));
            return entry == null ? null : entry.getValue();
        }

        @Override
        public V remove(Object key) {
            Entry<K, V> entry = map.remove(key);
            return entry == null ? null : entry.getValue();
        }

        @Override
        public void putAll(Map<? extends K, ? extends V> m) {
            for (Map.Entry<? extends K, ? extends V> e : m.entrySet()) {
                put(e.getKey(), e.getValue());
            }
        }

        @Override
        public void clear() {
            map.clear();
        }

        @Override
        public Set<K> keySet() {
            return map.keySet();
        }

        @Override
        public Collection<V> values() {
            return new AbstractCollection<V>() {
                private Collection<Entry<K,V>> coll = map.values();

                @Override
                public Iterator<V> iterator() {
                    return new Iterator<V>() {
                        private Iterator<Entry<K,V>> it = coll.iterator();

                        @Override
                        public boolean hasNext() {
                            return it.hasNext();
                        }

                        @Override
                        public void remove() {
                            it.remove();
                        }

                        @Override
                        public V next() {
                            return it.next().getValue();
                        }
                    };
                }

                @Override
                public int size() {
                    return coll.size();
                }
            };
        }

        @Override
        public Set<Entry<K, V>> entrySet() {
            return new AbstractSet<Entry<K,V>>() {
                private Set<Entry<K,Entry<K,V>>> set = map.entrySet();

                @Override
                public Iterator<Entry<K, V>> iterator() {
                    return new Iterator<Entry<K,V>>() {
                        private Iterator<Entry<K,Entry<K,V>>> it = set.iterator();

                        @Override
                        public boolean hasNext() {
                            return it.hasNext();
                        }

                        @Override
                        public void remove() {
                            it.remove();
                        }

                        @Override
                        public Entry<K, V> next() {
                            return it.next().getValue();
                        }
                    };
                }

                @Override
                public int size() {
                    return set.size();
                }
            };
        }

        @Override
        public int hashCode() {
            return map.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            @SuppressWarnings("unchecked")
            ArrayMapImpl<K,V> other = (ArrayMapImpl<K,V>) obj;
            return map.equals(other.map);
        }
    }

    @Override
    protected <K, V> Map<K, V> createMap() {
        return new ArrayMapImpl<>();
    }

    @Test
    public void shouldFindIndexOfKey() {
        ArrayListMap<String,Integer> map = new ArrayListMap<>(t -> "" + t);
        for (int i=0; i<10; i++) {
            map.add(i);
        }

        for (int i=0; i<10; i++) {
            assertEquals(i, map.indexOfKey("" + i));
        }
    }

    @Test
    public void shouldGetAtIndex() {
        ArrayListMap<String,Integer> map = new ArrayListMap<>(t -> "" + t);
        for (int i=0; i<10; i++) {
            map.add(i);
        }

        for (int i=0; i<10; i++) {
            assertEquals(i, map.getAtIndex(i), 0);
        }
    }

    @Test
    public void shouldFindIndexOfValue() {
        ArrayListMap<String,Integer> map = new ArrayListMap<>(t -> "" + t);
        for (int i=0; i<10; i++) {
            map.add(i);
        }

        for (int i=0; i<10; i++) {
            assertEquals(i, map.indexOfValueFrom(i, 0));
        }
    }

    @Test
    public void shouldRemoveAtIndexAtFirst() {
        ArrayListMap<String,Integer> map = new ArrayListMap<>(t -> "" + t);
        for (int i=0; i<10; i++) {
            map.add(i);
        }
        assertTrue(map.containsValue(0));

        map.removeAtIndex(0);

        assertFalse(map.containsValue(0));
    }

    @Test
    public void shouldRemoveAtIndexInMiddle() {
        ArrayListMap<String,Integer> map = new ArrayListMap<>(t -> "" + t);
        for (int i=0; i<10; i++) {
            map.add(i);
        }
        assertTrue(map.containsValue(5));

        map.removeAtIndex(5);

        assertFalse(map.containsValue(5));
    }

    @Test
    public void shouldRemoveAtIndexAtEnd() {
        ArrayListMap<String,Integer> map = new ArrayListMap<>(t -> "" + t);
        for (int i=0; i<10; i++) {
            map.add(i);
        }
        assertTrue(map.containsValue(9));

        map.removeAtIndex(9);

        assertFalse(map.containsValue(9));
    }

    @Test
    public void shouldPutAtIndexFirst() {
        ArrayListMap<String,Integer> map = new ArrayListMap<>(t -> "" + t);
        for (int i=0; i<10; i++) {
            map.add(i);
        }

        map.putAtIndex(0, 99);

        assertEquals(11, map.size());
        assertEquals(99, map.getAtIndex(0), 0);
        assertEquals(0, map.getAtIndex(1), 0);
    }

    @Test
    public void shouldPutAtIndexInMiddle() {
        ArrayListMap<String,Integer> map = new ArrayListMap<>(t -> "" + t);
        for (int i=0; i<10; i++) {
            map.add(i);
        }

        map.putAtIndex(5, 99);

        assertEquals(11, map.size());
        assertEquals(99, map.getAtIndex(5), 0);
        assertEquals(5, map.getAtIndex(6), 0);
    }

    @Test
    public void shouldPutAtIndexAtBeforeEnd() {
        ArrayListMap<String,Integer> map = new ArrayListMap<>(t -> "" + t);
        for (int i=0; i<10; i++) {
            map.add(i);
        }

        map.putAtIndex(9, 99);

        assertEquals(11, map.size());
        assertEquals(99, map.getAtIndex(9), 0);
        assertEquals(9, map.getAtIndex(10), 0);
    }

    @Test
    public void shouldPutAtIndexAtEnd() {
        ArrayListMap<String,Integer> map = new ArrayListMap<>(t -> "" + t);
        for (int i=0; i<10; i++) {
            map.add(i);
        }

        map.putAtIndex(10, 99);

        assertEquals(11, map.size());
        assertEquals(9, map.getAtIndex(9), 0);
        assertEquals(99, map.getAtIndex(10), 0);
    }
}
