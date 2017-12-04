package com.fillumina.performance.util.collection;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

/**
 * It's a map loaded over an array.
 * It maintains insertion order, caches hash codes, can access its elements by
 * index randomly and is very fast to clone.
 * It's insertion and extraction times are both O(N) but can be fast for few
 * items because of locality.
 * {@link #keyList()} items are accessed with O(1).
 * It uses {@link #Cursor} which is faster than a standard iterator but is not
 * compliant with {@link Map} specifications because every {@link Map.Entry}
 * returned is in fact the same object.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ArrayMap<K,V>
        implements Iterable<Entry<K,V>>, Map<K,V>, Cloneable, Serializable {

    public static final ArrayMap<?,?> EMPTY = new ArrayMap<>().unmodifiable();

    private static final long serialVersionUID = 1L;

    private Object[] array;
    private int[] hashes;
    private int size;

    private Set<Entry<K,V>> entrySet;
    private Set<K> keySet;
    private Collection<V> values;
    private List<K> keyList;
    private UnmodifiableView unmodifiableView;

    @SuppressWarnings("unchecked")
    public static <K,V> ArrayMap<K,V> emtpy() {
        return (ArrayMap<K, V>) EMPTY;
    }

    public ArrayMap() {
    }

    protected ArrayMap(Object[] array, int[] hashes, int size) {
        this.array = array;
        this.hashes = hashes;
        this.size = size;
    }

    @SuppressWarnings("unchecked")
    public static <K,V> ArrayMap<K,V> create(Object... objects) {
        final ArrayMap<K,V> map = new ArrayMap<>();
        for (int i=0; i<objects.length; i+=2) {
            map.put((K) objects[i], (V) objects[i+1]);
        }
        return map;
    }

    public <W> ArrayMap<K,W> transform(Function<V,W> converter) {
        ArrayMap<K,W> map = new ArrayMap<>();
        for (Map.Entry<K,V> t : this) {
            map.put(t.getKey(), converter.apply(t.getValue()));
        }
        return map;
    }

    @SuppressWarnings("unchecked")
    public K getKeyAtIndex(int index) {
        if (index >= size()) {
            throw new IndexOutOfBoundsException("size= " + size());
        }
        return (K) array[index << 1];
    }

    @SuppressWarnings("unchecked")
    public V getValueAtIndex(int index) {
        if (index >= size()) {
            throw new IndexOutOfBoundsException("size= " + size());
        }
        return (V) array[(index << 1) + 1];
    }

    public Entry<K,V> getEntryAtIndex(int index) {
        return new Cursor(index << 1);
    }

    public Cursor getCursorAtIndex(int index) {
        return new Cursor(index << 1);
    }

    public class UnmodifiableView extends ArrayMap<K,V> {
        private static final long serialVersionUID = 1L;

        private UnmodifiableView(Object[] array, int[] hashes, int size) {
            super(array, hashes, size);
        }

        private void relink() {
            if (super.array != ArrayMap.this.array) {
                super.array = ArrayMap.this.array;
                super.hashes = ArrayMap.this.hashes;
                super.size = ArrayMap.this.size;
            }
        }

        @Override
        public boolean isUnmodifiable() {
            return true;
        }

        @Override
        public int size() {
            relink();
            return super.size();
        }

        @Override
        protected int getIndexOf(Object key) {
            relink();
            return super.getIndexOf(key);
        }

        @Override
        public Iterator<Entry<K, V>> iterator() {
            relink();
            return super.iterator();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Override
        protected void removeIndex(int index) {
            throw new UnsupportedOperationException();
        }

        @Override
        public V put(K key, V value) {
            throw new UnsupportedOperationException();
        }
    }

    public boolean isUnmodifiable() {
        return false;
    }

    public ArrayMap<K,V> unmodifiable() {
        if (isUnmodifiable()) {
            return this;
        }
        if (unmodifiableView == null) {
            unmodifiableView = new UnmodifiableView(array, hashes, size);
        }
        return unmodifiableView;
    }

    @Override
    public ArrayMap<K,V> clone() throws CloneNotSupportedException {
        if (array == null) {
            return new ArrayMap<>(null, null, 0);
        }
        return new ArrayMap<>(array.clone(), hashes, size);
    }

    @Override
    public int size() {
        return size >> 1;
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    /** Useful with fluid interface initializations. */
    public ArrayMap<K,V> add(K key, V value) {
        put(key, value);
        return this;
    }

    @Override
    public V put(K key, V value) {
        int index = getIndexOf(key);
        if (index != -1) {
            @SuppressWarnings("unchecked")
            V tmpValue = (V) array[index + 1];
            array[index + 1] = value;
            return tmpValue;
        }
        if (array == null) {
            array = new Object[8];
            hashes = new int[4];
        } else if (array.length < size + 1) {
            Object[] tmpArray = new Object[array.length << 1];
            System.arraycopy(array, 0, tmpArray, 0, size);
            array = tmpArray;
            int[] tmpHashes = new int[array.length >> 1];
            System.arraycopy(hashes, 0, tmpHashes, 0, hashes.length);
            hashes = tmpHashes;
        }
        array[size] = key;
        array[size + 1] = value;
        hashes[size >> 1] = Objects.hashCode(key);
        size += 2;
        return null;
    }

    protected int getIndexOf(Object key) {
        if (array == null) {
            return -1;
        }
        int hashcode = key.hashCode();
        for (int i=0,l=hashes.length; i<l; i++) {
            if (hashcode == hashes[i]) {
                @SuppressWarnings("unchecked")
                K k = (K) array[i << 1];
                if (Objects.equals(k, key)) {
                    return i << 1;
                }
            }
        }
        return -1;
    }

    @Override
    @SuppressWarnings("unchecked")
    public V get(Object key) {
        int index = getIndexOf(key);
        if (index == -1) {
            return null;
        }
        return (V) array[index + 1];
    }

    @Override
    public V remove(Object key) {
        int index = getIndexOf(key);
        if (index == -1) {
            return null;
        }
        @SuppressWarnings("unchecked")
        V v = (V) array[index + 1];
        removeIndex(index);
        return v;
    }

    protected void removeIndex(int index) {
        if (array == null || index < 0 || index > size) {
            throw new IllegalStateException();
        }
        System.arraycopy(array, index + 2, array, index, array.length - index - 2);
        int h = index >> 1;
        System.arraycopy(hashes, h + 1, hashes, h, hashes.length - h - 1);
        size-=2;
    }

    @Override
    public boolean containsKey(Object key) {
        return getIndexOf(key) != -1;
    }

    @Override
    public boolean containsValue(Object value) {
        if (array == null) {
            return false;
        }
        for (int i=1,l=size; i<l; i+=2) {
            @SuppressWarnings("unchecked")
            V v = (V) array[i];
            if (Objects.equals(v, value)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        m.forEach((k,v) -> put(k,v));
    }

    @Override
    public void clear() {
        hashes = null;
        array = null;
        size = 0;
    }

    public class Cursor implements Entry<K,V>, Iterator<Entry<K,V>> {
        private int index = -2;
        private boolean removed;

        public Cursor() {}

        public Cursor(int index) {
            this.index = index;
        }

        public void setIndex(int index) {
            this.index = (index << 1);
        }

        @Override
        @SuppressWarnings("unchecked")
        public K getKey() {
            return (K) array[index];
        }

        @Override
        @SuppressWarnings("unchecked")
        public V getValue() {
            return (V) array[index + 1];
        }

        @Override
        public V setValue(V value) {
            @SuppressWarnings("unchecked")
            V v = (V) array[index + 1];
            array[index + 1] = value;
            return v;
        }

        @Override
        public boolean hasNext() {
            return index < size - 2;
        }

        @Override
        public Entry<K, V> next() {
            removed = false;
            index += 2;
            return this;
        }

        @Override
        public void remove() {
            if (removed) {
                throw new IllegalStateException();
            }
            removeIndex(index);
            index -= 2;
            removed = true;
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 29 * hash + this.index;
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
            final Cursor other = (Cursor) obj;
            if (this.index != other.index) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return "{" + Objects.toString(getKey()) + " = " +
                    Objects.toString(getValue()) + "}";
        }
    }

    @Override
    public Iterator<Entry<K, V>> iterator() {
        return new Cursor();
    }

    private abstract class View<T> extends AbstractSet<T> {
        abstract T select(Entry<K,V> entry);

        @Override
        public Iterator<T> iterator() {
            return new Iterator<T>() {
                // so that UnmodifiableView can detect cursor calling
                @SuppressWarnings("unchecked")
                private final Cursor cursor = (Cursor) ArrayMap.this.iterator();

                @Override
                public boolean hasNext() {
                    return cursor.hasNext();
                }

                @Override
                public T next() {
                    return select(cursor.next());
                }

                @Override
                public void remove() {
                    cursor.remove();
                }
            };
        }

        @Override
        public int size() {
            return size >> 1;
        }
    }

    private class UnmodifiableKeyList extends AbstractList<K> {
        @Override
        public int size() {
            return size >> 1;
        }

        @Override
        @SuppressWarnings("unchecked")
        public K get(int index) {
            return (K) array[index << 1];
        }
    }

    private class EntrySet extends View<Entry<K,V>> {
        @Override Entry<K, V> select(Entry<K, V> entry) { return entry; }
    }

    private class KeySet extends View<K> {
        @Override K select(Entry<K, V> entry) { return entry.getKey(); }
    }

    private class Values extends View<V> {
        @Override V select(Entry<K, V> entry) { return entry.getValue(); }
    }

    public List<K> keyList() {
        if (keyList == null) {
            keyList = new UnmodifiableKeyList();
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
    public Collection<V> values() {
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

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 31 * hash + Arrays.deepHashCode(this.array);
        hash = 31 * hash + this.size;
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
        if (this.size != other.size) {
            return false;
        }
        if (!Arrays.deepEquals(this.array, other.array)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        Cursor i = new Cursor();
        if (! i.hasNext()) {
            return "{}";
        }
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        for (;;) {
            Entry<K,V> e = i.next();
            K key = e.getKey();
            V value = e.getValue();
            sb.append(key   == this ? "(this Map)" : key);
            sb.append('=');
            sb.append(value == this ? "(this Map)" : value);
            if (! i.hasNext()) {
                return sb.append('}').toString();
            }
            sb.append(',').append(' ');
        }
    }

}
