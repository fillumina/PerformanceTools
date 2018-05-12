package com.fillumina.performance.util.collection;

import com.fillumina.performance.util.collection.DoubleLinkedIndexes.FastIterator;
import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Fast indexed hash map implementation (similar in features to
 * {@link LinkedHashMap} but faster to clone).
 * <ul>
 * <li>all operations are O(1) on average
 * <li>worse case is linear O(N)
 * <li>increases and decreases its size automatically
 * <li>maintains insertion order
 * <li>very fast to clone
 * <li>uses fast Cursor iteration
 * <li>doesn't accept null as key
 * <li>manages its own unmodifiable version of itself
 * <li>has copy constructor and clone constructor
 * <li>improves locality of access by using arrays
 * </ul>
 * {@link #Cursor} is faster than a standard iterator but it is not
 * compliant with {@link Map} specifications because every {@link Map.Entry}
 * returned is in fact the same object.
 * <br>
 * Avoid using {@link #entrySet()} because to be compliant with the specs
 * it must create a new {@link Map.Entry} for each access.
 * Use map's {@link #iterator()} or {@link #cursor()} instead which return a
 * {@link #Cursor}.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class LinkedArrayMap<K,V>
        implements Iterable<Entry<K,V>>, Map<K,V>, Cloneable, Serializable {

    public static final LinkedArrayMap<?,?> EMPTY =
            new LinkedArrayMap<>().unmodifiable();

    private static final long serialVersionUID = 1L;

    private DoubleLinkedIndexes dlIndexes;
    private Object[] array; // [key, value]
    private int[] indexes;  // [index, hashcode]
    private int size;       // actual size * 2

    private Set<Entry<K,V>> entrySet;
    private Set<K> keySet;
    private Collection<V> values;
    private UnmodifiableView<K,V> unmodifiableView;

    @SuppressWarnings("unchecked")
    public static <K,V> LinkedArrayMap<K,V> emtpy() {
        return (LinkedArrayMap<K, V>) EMPTY;
    }

    public LinkedArrayMap() {
    }

    public LinkedArrayMap(Map<? extends K, ? extends V> copy) {
        putAll(copy);
    }

    public LinkedArrayMap(LinkedArrayMap<? extends K, ? extends V> clone) {
        if (clone.array != null) {
            this.array = clone.array.clone();
            this.indexes = clone.indexes.clone();
            this.size = clone.size;
            this.dlIndexes = clone.dlIndexes.clone();
        }
    }

    public LinkedArrayMap(int initialSize) {
        if (initialSize < 0) {
            throw new IllegalArgumentException(
                    "initial size must be > 0, was " + initialSize);
        }
        int length = roundUpToPowerOf2(initialSize);
        this.array = new Object[length];
        this.indexes = new int[length << 1]; // 50% fill
        this.dlIndexes = new DoubleLinkedIndexes(length);
    }

    private static int roundUpToPowerOf2(int number) {
        // assert number >= 0 : "number must be non-negative";
        return (number > 1) ? Integer.highestOneBit((number - 1) << 1) : 2; //1;
    }

    protected LinkedArrayMap(Object[] array,
            int[] hashes,
            int size,
            DoubleLinkedIndexes dlIndexes) {
        this.array = array;
        this.indexes = hashes;
        this.size = size;
        this.dlIndexes = dlIndexes;
    }

    /**
     * Builder to create a map from key,value pairs.
     *
     * @param objects pairs of key, value
     * @return the map
     */
    @SuppressWarnings("unchecked")
    public static <K,V> LinkedArrayMap<K,V> create(Object... objects) {
        final LinkedArrayMap<K,V> map = new LinkedArrayMap<>();
        for (int i=0; i<objects.length; i+=2) {
            map.put((K) objects[i], (V) objects[i+1]);
        }
        return map;
    }

    /** Converts to another map. */
    public <W> LinkedArrayMap<K,W> transform(Function<V,W> converter) {
        LinkedArrayMap<K,W> map = new LinkedArrayMap<>();
        for (Map.Entry<K,V> t : this) {
            map.put(t.getKey(), converter.apply(t.getValue()));
        }
        return map;
    }

    /**
     * Override if you need a different equals().
     * @param a the given object
     * @param b the internal key
     */
    public boolean equals(Object a, Object b) {
        return Objects.equals(a, b);
    }

    //TODO test unmodifiable!
    public static class UnmodifiableView<K,V> extends LinkedArrayMap<K,V> {
        private static final long serialVersionUID = 1L;

        private LinkedArrayMap<K,V> delegate;

        public UnmodifiableView(LinkedArrayMap<K,V> delegate) {
            super();
            this.delegate = delegate;
        }

        @Override
        public boolean isUnmodifiable() {
            return true;
        }

        @Override
        public void ensureCapacity(int requiredCapacity) {
            throw new UnsupportedOperationException();
        }

        @Override
        public LinkedArrayMap<K, V> clone() {
            return this;
        }

        @Override
        public int getIndexOfKey(Object key) {
            return delegate.getIndexOfKey(key);
        }

        @Override
        public V get(Object key) {
            return delegate.get(key);
        }

        @Override
        public int size() {
            return delegate.size();
        }

        @Override
        public boolean containsValue(Object value) {
            return delegate.containsValue(value);
        }

        @Override
        public boolean containsKey(Object key) {
            return delegate.containsKey(key);
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Override
        public V remove(Object key) {
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

    public LinkedArrayMap<K,V> unmodifiable() {
        if (isUnmodifiable()) {
            return this;
        }
        if (unmodifiableView == null) {
            unmodifiableView = new UnmodifiableView<>(this);
        }
        return unmodifiableView;
    }

    @Override
    public LinkedArrayMap<K,V> clone() {
        if (array == null) {
            return new LinkedArrayMap<>();
        }
        return new LinkedArrayMap<>(this);
    }

    public void ensureCapacity(int requiredCapacity) {
        int available = (array == null) ? 0 : ((array.length - size) >> 1);
        if (requiredCapacity > available) {
            int newSize = roundUpToPowerOf2((size >> 1) + requiredCapacity);
            resize(newSize);
        }
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
    public LinkedArrayMap<K,V> add(K key, V value) {
        put(key, value);
        return this;
    }

    @Override
    public V put(K key, V value) {
        if (array == null) {
            array = new Object[8];
            indexes = new int[16]; // 50% max fill
            dlIndexes = new DoubleLinkedIndexes(8);
        } else if (size == array.length) {
            resize(indexes.length << 1);
        }
        int mask = getMask();
        int hashcode = key.hashCode();
        int bucket = hashcode & mask;
        while (true) {
            int pointer = indexes[bucket + 1];
            if (pointer == 0) {
                // bucket free, use it
                indexes[bucket] = hashcode;
                int idx = dlIndexes.addLast();
                //indexes[bucket + 1] = size + 1; // points to value
                indexes[bucket + 1] = idx + 1; // points to value
                array[idx] = key;
                array[idx + 1] = value;
                size += 2;
                return null;
            }
            if (hashcode == indexes[bucket]) {
                if (equals(key, array[pointer - 1])) {
                    // bucket used, right key, set value
                    @SuppressWarnings("unchecked")
                    V oldValue = (V) array[pointer];
                    array[pointer] = value;
                    return oldValue;
                }
            }
            bucket = (bucket + 2) & mask;
        }
    }

    private int getMask() {
        return (indexes.length - 1) & (~1);
    }

    @SuppressWarnings("unchecked")
    private void resize(int newSize) {
        resize(newSize, -1);
    }

    private void resize(int newSize, int butIndex) {
        LinkedArrayMap<K,V> newMap = new LinkedArrayMap<>(newSize);
        for (int i=0; i<size; i+=2) {
            if (i != butIndex) {
                newMap.put((K)array[i], (V)array[i+1]);
            }
        }
        this.array = newMap.array;
        this.indexes = newMap.indexes;
        this.dlIndexes = newMap.dlIndexes;
    }

    @Override
    @SuppressWarnings("unchecked")
    public V get(Object key) {
        if (size == 0) {
            return null;
        }
        int mask = getMask();
        int hashcode = key.hashCode();
        int bucket = hashcode & mask;
        while (true) {
            int pointer = indexes[bucket + 1];
            if (pointer == 0) {
                return null; // not found
            }
            if (hashcode == indexes[bucket]) {
                if (equals(key, array[pointer - 1])) {
                    return (V) array[pointer];
                }
            }
            bucket = (bucket + 2) & mask;
        }
    }

    @Override
    public boolean containsKey(Object key) {
        return getIndexOfKey(key) != -1;
    }

    public int getIndexOfKey(Object key) {
        if (array == null || size == 0) {
            return -1;
        }
        int mask = getMask();
        int hashcode = key.hashCode();
        int bucket = hashcode & mask;
        while (true) {
            int pointer = indexes[bucket + 1];
            if (pointer == 0) {
                return -1; // not found
            }
            if (hashcode == indexes[bucket]) {
                if (equals(key, array[pointer - 1])) {
                    return (pointer - 1) >> 1;
                }
            }
            bucket = (bucket + 2) & mask;
        }
    }

    @Override
    public V remove(Object key) {
        if (array == null) {
            return null;
        }
        int mask = getMask();
        int hashcode = key.hashCode();
        int bucket = hashcode & mask;
        while (true) {
            int pointer = indexes[bucket + 1];
            if (pointer == 0) {
                return null; // not found
            }
            if (hashcode == indexes[bucket]) {
                if (equals(key, array[pointer - 1])) {
                    int index = pointer - 1;
                    @SuppressWarnings("unchecked")
                    V oldValue = (V) array[pointer];

                    removeIndex(index, bucket, mask);
                    return oldValue;
                }
            }
            bucket = (bucket + 2) & mask;
        }
    }

    /**
     *
     * @param index         the index of k,v
     * @param bucket the    the bucket in the h,i
     * @param mask
     */
    private void removeIndex(int index, int bucket, int mask) {
        size -= 2;
        if (size < (indexes.length >> 2)) {
            resize(indexes.length >> 1, index);

        } else {
            // null references to let GC eventually reclaims
            array[index] = null;
            array[index + 1] = null;

            // free the bucket
            indexes[bucket + 1] = 0;

            // unlink the element
            dlIndexes.remove(index);

            // rolls subsequent buckets back if needed
            int b = bucket;
            while (true) {
                b = (b + 2) & mask;
                int p = indexes[b + 1];
                if (p == 0) {
                    break;
                }
                int h = indexes[b];
                int w = h & mask; // where it want to be
                if (b != w) {
                    while (indexes[w + 1] != 0) {
                        w = (w + 2) & mask;
                    }
                    // relocate the bucket
                    indexes[w] = indexes[b];
                    indexes[w + 1] = indexes[b + 1];
                    indexes[b + 1] = 0; // free the former bucket
                }
            }
        }
    }

    @Override
    public boolean containsValue(Object value) {
        if (array == null) {
            return false;
        }
        for (int i=1,l=size; i<l; i+=2) {
            @SuppressWarnings("unchecked")
            V v = (V) array[i];
            if (equals(value, v)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        ensureCapacity(m.size());
        m.forEach((k,v) -> put(k,v));
    }

    @Override
    public void clear() {
        indexes = null;
        array = null;
        size = 0;
        dlIndexes = null;
    }

    private class EntryImpl implements Map.Entry<K,V> {
        private final K key;

        public EntryImpl(K key) {
            this.key = key;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return LinkedArrayMap.this.get(key);
        }

        @Override
        public V setValue(V value) {
            return LinkedArrayMap.this.put(key, value);
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 11 * hash + Objects.hashCode(this.key);
            hash = 11 * hash + Objects.hashCode(getValue());
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
            final Entry<?,?> other = (Entry<?,?>) obj;
            if (!Objects.equals(this.key, other.getKey())) {
                return false;
            }
            if (!Objects.equals(getValue(), other.getValue())) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return key + " = " + Objects.toString(LinkedArrayMap.this.get(key));
        }
    }

    /** WARNING! this is a <b>mutable</b> object! */
    public class Cursor implements Entry<K,V>, Iterator<Entry<K,V>> {
        private final FastIterator fit;
        private boolean removed;

        public Cursor() {
            fit = dlIndexes == null ? null : dlIndexes.fastIterator();
        }

        private Entry<K,V> createEntry() {
            return new EntryImpl(getKey());
        }

        @Override
        @SuppressWarnings("unchecked")
        public K getKey() {
            return (K) array[fit.current()];
        }

        @Override
        @SuppressWarnings("unchecked")
        public V getValue() {
            return (V) array[fit.current() + 1];
        }

        @Override
        public V setValue(V value) {
            @SuppressWarnings("unchecked")
            V oldValue = (V) array[fit.current() + 1];
            array[fit.current() + 1] = value;
            return oldValue;
        }

        @Override
        public boolean hasNext() {
            return fit.hasNext();
        }

        @Override
        public Cursor next() {
            removed = false;
            fit.next();
            return this;
        }

        @Override
        @SuppressWarnings("unchecked")
        public void remove() {
            if (removed || fit == null) {
                throw new IllegalStateException("element already removed");
            }
            int idx = fit.current();
            if (idx == -1) {
                throw new IllegalStateException("next() must be called first");
            }
            LinkedArrayMap.this.remove((K) array[idx]);
            removed = true;
        }

        @Override
        public int hashCode() {
            return Objects.hash(getKey(), getValue());
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
            @SuppressWarnings("unchecked")
            final Entry<K,V> other = (Entry<K,V>) obj;
            return (!getKey().equals(other.getKey()) ||
                    !getValue().equals(other.getValue()));
        }

        @Override
        public String toString() {
            return getKey().toString() + " => " + Objects.toString(getValue());
        }
    }

    public Cursor cursor() {
        return new Cursor();
    }

    @Override
    public Iterator<Entry<K, V>> iterator() {
        return new Cursor();
    }

    private abstract class View<T> extends AbstractSet<T> {
        abstract T select(Cursor entry);

        @Override
        public Iterator<T> iterator() {
            return new Iterator<T>() {
                @SuppressWarnings("unchecked")
                private final Cursor cursor = (Cursor) LinkedArrayMap.this.iterator();

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
            return LinkedArrayMap.this.size();
        }
    }

    private class EntrySet extends View<Entry<K,V>> {
        @Override
        Entry<K, V> select(Cursor entry) {
            return entry.createEntry();
        }
    }

    private class KeySet extends View<K> {
        @Override K select(Cursor entry) { return entry.getKey(); }
    }

    private class Values extends View<V> {
        @Override V select(Cursor entry) { return entry.getValue(); }
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

    /**
     * WARNING: using this set means that a new {@link Map.Entry} must be
     * created at each access.
     */
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
        Cursor cursor = cursor();
        while (cursor.hasNext()) {
            hash = 31 * hash + cursor.next().hashCode();
        }
        return hash;
    }

    /** Overridden to use cursor instead of {@link #entrySet()}. */
    @Override
    public void forEach(BiConsumer<? super K, ? super V> action) {
        for (Map.Entry<K, V> e : this) {
            action.accept(e.getKey(), e.getValue());
        }
    }

    /** Overridden to use cursor instead of {@link #entrySet()}. */
    @Override
    public void replaceAll(
            BiFunction<? super K, ? super V, ? extends V> function) {
        for (Map.Entry<K, V> e : this) {
            V v = function.apply(e.getKey(), e.getValue());
            e.setValue(v);
        }
    }

    /**
     * Compares the specified object with this map for equality.  Returns
     * <tt>true</tt> if the given object is also a map and the two maps
     * represent the same mappings.  More formally, two maps <tt>m1</tt> and
     * <tt>m2</tt> represent the same mappings if
     * <tt>m1.entrySet().equals(m2.entrySet())</tt>.  This ensures that the
     * <tt>equals</tt> method works properly across different implementations
     * of the <tt>Map</tt> interface.
     *
     * @implSpec
     * This implementation first checks if the specified object is this map;
     * if so it returns <tt>true</tt>.  Then, it checks if the specified
     * object is a map whose size is identical to the size of this map; if
     * not, it returns <tt>false</tt>.  If so, it iterates over this map's
     * <tt>entrySet</tt> collection, and checks that the specified map
     * contains each mapping that this map contains.  If the specified map
     * fails to contain such a mapping, <tt>false</tt> is returned.  If the
     * iteration completes, <tt>true</tt> is returned.
     * <br>
     * Copied from {@link java.util.AbstractMap#equals(Object)}.
     *
     * @param object object to be compared for equality with this map
     * @return <tt>true</tt> if the specified object is equal to this map
     */
    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }

        if (!(object instanceof Map)) {
            return false;
        }
        Map<?,?> other = (Map<?,?>) object;
        if (other.size() != size()) {
            return false;
        }

        try {
            Iterator<Entry<K,V>> i = cursor();
            while (i.hasNext()) {
                Entry<K,V> e = i.next();
                K key = e.getKey();
                V value = e.getValue();
                if (value == null) {
                    if (!(other.get(key)==null && other.containsKey(key))) {
                        return false;
                    }
                } else {
                    if (!value.equals(other.get(key))) {
                        return false;
                    }
                }
            }
        } catch (ClassCastException | NullPointerException unused) {
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
