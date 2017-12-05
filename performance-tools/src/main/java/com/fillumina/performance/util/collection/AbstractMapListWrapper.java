package com.fillumina.performance.util.collection;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
 * keys from values. It keeps inserting order.
 * It's size efficient and reasonably fast for a relatively few entries
 * (it's backed by an {@link ArrayList} so most operations take linear time).
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class AbstractMapListWrapper
            <I extends AbstractMapListWrapper<I,K,V>, K,V>
        implements Map<K,V>, Iterable<Entry<K,V>>, Serializable {
    private static final long serialVersionUID = 1L;

    private static class MapListWrapper<K,V>
            extends AbstractMapListWrapper<MapListWrapper<K,V>,K,V> {
        private static final long serialVersionUID = 1L;

        private final Function<V,K> keyExtractor;

        public MapListWrapper(Function<V,K> keyExtractor) {
            this.keyExtractor = keyExtractor;
        }

        private MapListWrapper(Function<V, K> keyExtractor, List<V> list) {
            super(list);
            this.keyExtractor = keyExtractor;
        }

        @Override
        protected K getKeyFromValue(V value) {
            return keyExtractor.apply(value);
        }

        @Override
        protected MapListWrapper<K,V> createNew(List<V> list) {
            return new MapListWrapper<>(keyExtractor, list);
        }
    }

    private final List<V> list;

    private KeySet keySet;
    private KeyList keyList;
    private Values values;
    private EntrySet entrySet;
    private I unmodifiable;

    public static <K,V> AbstractMapListWrapper<?,K,V> create(
            Function<V,K> keyExtractor) {
        return new MapListWrapper<>(keyExtractor);
    }

    public AbstractMapListWrapper() {
        this(10);
    }

    public AbstractMapListWrapper(AbstractMapListWrapper<I,K,V> copy) {
        this.list = new ArrayList<>(copy.list);
    }

    public AbstractMapListWrapper(int size) {
        this.list = new ArrayList<>(size);
    }

    protected AbstractMapListWrapper(List<V> direct) {
        this.list = direct;
    }

    protected abstract K getKeyFromValue(V value);
    protected abstract I createNew(List<V> list);

    @SuppressWarnings("unchecked")
    public I add(V... values) {
        if (values != null || values.length > 0) {
            for (V v : values) {
                put(v);
            }
        }
        return (I) this;
    }

    public void addAll(Collection<V> coll) {
        if (coll != null && !coll.isEmpty()) {
            coll.forEach( (V v) -> put(v) );
        }
    }

    public boolean isUnmodifiable() {
        int lastIndex = size() - 1;
        try {
            V lastItem = list.get(lastIndex);
            list.remove(lastIndex);
            list.add(lastItem);
            return false;
        } catch (UnsupportedOperationException e) {
            return true;
        }
    }

    public I unmodifiable() {
        if (isUnmodifiable()) {
            return (I) this;
        }
        if (unmodifiable == null) {
            unmodifiable = createNew(Collections.unmodifiableList(list));
        }
        return unmodifiable;
    }

    public void trimToSize() {
        if (list instanceof ArrayList) {
            ((ArrayList)list).trimToSize();
        }
    }

    public int indexOfKey(K key) {
        int s = list.size();
        for (int i=0; i<s; i++) {
            V v = list.get(i);
            if (Objects.equals(key, getKeyFromValue(v))) {
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
        int idx = indexOfKey(getKeyFromValue(value));
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
            if (Objects.equals(key, getKeyFromValue(v))) {
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
    public V findByKey(Predicate<K> predicate) {
        for (V v : list) {
            if (predicate.test(getKeyFromValue(v))) {
                return v;
            }
        }
        return null;
    }

    /** Searches an element by specifying a predicate over values. */
    public V findByValue(Predicate<V> predicate) {
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
            if (Objects.equals(key, getKeyFromValue(v))) {
                return v;
            }
        }
        return null;
    }

    public V put(V value) {
        return put(getKeyFromValue(value), value);
    }

    @Override
    public V put(K key, V value) {
        ListIterator<V> it = list.listIterator();
        while (it.hasNext()) {
            V v = it.next();
            if (Objects.equals(key, getKeyFromValue(v))) {
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
            if (Objects.equals(key, getKeyFromValue(v))) {
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
        if (keyList == null) {
            keyList = new KeyList();
        }
        return keyList;
    }

    @Override
    public Set<K> keySet() {
        if (keySet == null) {
            keySet = new KeySet();
        }
        return keySet;
    }

    @Override
    public List<V> values() {
        if (values == null) {
            values = new Values();
        }
        return values;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        if (entrySet == null) {
            entrySet = new EntrySet();
        }
        return entrySet;
    }

    private class Cursor implements Entry<K,V>, Iterator<Entry<K,V>> {
        private final ListIterator<V> it = list.listIterator();
        private V v;

        @Override
        public K getKey() {
            return getKeyFromValue(v);
        }

        @Override
        public V getValue() {
            return v;
        }

        @Override
        public V setValue(V value) {
            V oldv = v;
            if (!containsKey(getKeyFromValue(value))) {
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
        final AbstractMapListWrapper<?,?,?> other =
                (AbstractMapListWrapper<?,?,?>) obj;
        if (!Objects.equals(this.list, other.list)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + list.toString();
    }

    private class EntrySet extends AbstractSet<Entry<K, V>> {
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
    }

    private class Values extends AbstractList<V> {
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
    }

    private class KeySet extends AbstractSet<K> {

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
                    return getKeyFromValue(it.next());
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
    }

    private class KeyList extends AbstractList<K> {

        @Override
        public K remove(int index) {
            V v = list.remove(index);
            return v == null ? null : getKeyFromValue(v);
        }

        @Override
        public K get(int index) {
            V v = list.get(index);
            return v == null ? null : getKeyFromValue(v);
        }

        @Override
        public void clear() {
            list.clear();
        }

        @Override
        public int size() {
            return list.size();
        }
    }
}
