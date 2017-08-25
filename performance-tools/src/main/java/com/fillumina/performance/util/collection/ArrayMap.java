package com.fillumina.performance.util.collection;

import java.util.AbstractList;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A {@link Map} implementation which uses a given mapping function to extract
 keys from values (implicitly creating an entry out of each v).
 * It keeps inserting order.
 * It's size efficient and reasonably fast for a relatively few entries.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ArrayMap<K,V>
        implements Map<K,V>, Iterable<Entry<K,V>> {

    private final Function<V,K> keyExtractor;
    private final ArrayList<V> list;

    public ArrayMap(Function<V, K> keyExtractor) {
        this(keyExtractor, 10);
    }

    public ArrayMap(Function<V, K> keyExtractor, int size) {
        this.keyExtractor = keyExtractor;
        this.list = new ArrayList<>(size);
    }

    public ArrayMap(Function<V, K> keyExtractor, List<V> list) {
        this.keyExtractor = keyExtractor;
        this.list = new ArrayList<>(list.size());
        // cannot just copy because there could be key clashing
        addAll(list);
    }

    public ArrayMap<K,V> add(V... values) {
        for (V v : values) {
            put(v);
        }
        return this;
    }

    public void trimToSize() {
        list.trimToSize();
    }

    public int indexOfKey(K key) {
        int s = list.size();
        for (int i=0; i<s; i++) {
            V v = list.get(i);
            if (Objects.equals(key, keyExtractor.apply(v))) {
                return i;
            }
        }
        return -1;
    }

    public int indexOfValue(V value) {
        return list.indexOf(value);
    }

    public int indexOfValueFrom(V value, int start) {
        return list.subList(start, list.size()).indexOf(value);
    }

    public V getAtIndex(int index) {
        return list.get(index);
    }

    public V removeAtIndex(int index) {
        return list.remove(index);
    }

    public void putAtIndex(int index, V value) {
        int idx = indexOfKey(keyExtractor.apply(value));
        if (idx != -1) {
            list.remove(idx);
        }
        list.add(index, value);
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        for (V v : list) {
            if (Objects.equals(key, keyExtractor.apply(v))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        return list.contains(value);
    }

    /** Searches an element by specifying a predicate over keys. */
    public V getByKey(Predicate<K> predicate) {
        for (V v : list) {
            if (predicate.test(keyExtractor.apply(v))) {
                return v;
            }
        }
        return null;
    }

    /** Searches an element by specifying a predicate over values. */
    public V getByValue(Predicate<V> predicate) {
        for (V v : list) {
            if (predicate.test(v)) {
                return v;
            }
        }
        return null;
    }

    @Override
    public V get(Object key) {
        for (V v : list) {
            if (Objects.equals(key, keyExtractor.apply(v))) {
                return v;
            }
        }
        return null;
    }

    public void addAll(Collection<V> values) {
        for (V v : values) {
            put(v);
        }
    }

    public V put(V value) {
        return put(keyExtractor.apply(value), value);
    }

    @Override
    public V put(K key, V value) {
        ListIterator<V> it = list.listIterator();
        while (it.hasNext()) {
            V v = it.next();
            if (Objects.equals(key, keyExtractor.apply(v))) {
                it.set(value);
                return v;
            }
        }
        list.add(value);
        return null;
    }

    @Override
    public V remove(Object key) {
        Iterator<V> it = list.iterator();
        while (it.hasNext()) {
            V v = it.next();
            if (Objects.equals(key, keyExtractor.apply(v))) {
                it.remove();
                return v;
            }
        }
        return null;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        for (Entry<? extends K, ? extends V> e : m.entrySet()) {
            put(e.getKey(), e.getValue());
        }
    }

    @Override
    public void clear() {
        list.clear();
    }

    public List<K> keyList() {
        return new AbstractList<K>() {
            @Override
            public K remove(int index) {
                V v = list.remove(index);
                return v == null ? null : keyExtractor.apply(v);
            }

            @Override
            public K get(int index) {
                V v = list.get(index);
                return v == null ? null : keyExtractor.apply(v);
            }

            @Override
            public void clear() {
                list.clear();
            }

            @Override
            public int size() {
                return list.size();
            }
        };
    }

    @Override
    public Set<K> keySet() {
        // can remove but not add elements
        return new AbstractSet<K>() {
            @Override
            public Iterator<K> iterator() {
                return new Iterator<K>() {
                    private Iterator<V> it = list.iterator();

                    @Override
                    public boolean hasNext() {
                        return it.hasNext();
                    }

                    @Override
                    public K next() {
                        return keyExtractor.apply(it.next());
                    }

                    @Override
                    public void remove() {
                        it.remove();
                    }
                };
            }

            @Override
            public boolean remove(Object o) {
                @SuppressWarnings("unchecked")
                int idx = indexOfKey((K)o);
                if (idx != -1) {
                    list.remove(idx);
                    return true;
                }
                return false;
            }

            @Override
            public void clear() {
                list.clear();
            }

            @Override
            public int size() {
                return list.size();
            }
        };
    }

    @Override
    public List<V> values() {
        // can remove but not add elements
        return new AbstractList<V>() {
            @Override
            public V remove(int index) {
                return list.remove(index);
            }

            @Override
            public V get(int index) {
                return list.get(index);
            }

            @Override
            public void clear() {
                list.clear();
            }

            @Override
            public int size() {
                return list.size();
            }
        };
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return new AbstractSet<Entry<K,V>>() {
            @Override
            public Iterator<Entry<K, V>> iterator() {
                return new Cursor();
            }

            @Override
            public void clear() {
                list.clear();
            }

            @Override
            public int size() {
                return list.size();
            }
        };
    }

    private class Cursor implements Entry<K,V>, Iterator<Entry<K,V>> {
        private ListIterator<V> it = list.listIterator();
        private V v;

        @Override
        public K getKey() {
            return keyExtractor.apply(v);
        }

        @Override
        public V getValue() {
            return v;
        }

        @Override
        public V setValue(V value) {
            V oldv = v;
            if (!containsKey(keyExtractor.apply(value))) {
                it.set(value);
                v = value;
                return oldv;
            }
            return null;
        }

        @Override
        public boolean hasNext() {
            return it.hasNext();
        }

        @Override
        public Entry<K, V> next() {
            v = it.next();
            return this;
        }

        @Override
        public void remove() {
            it.remove();
        }
    }

    /**
     * It returns a <b>cursor</b> (the returned {@link java.util.Map.Entry}
     * is shared between all the returned instances and only its values changes.
     */
    @Override
    public Iterator<Entry<K, V>> iterator() {
        return new Cursor();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + Objects.hashCode(this.list);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ArrayMap<?, ?> other = (ArrayMap<?, ?>) obj;
        if (!Objects.equals(this.list, other.list)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + list.toString();
    }
}
