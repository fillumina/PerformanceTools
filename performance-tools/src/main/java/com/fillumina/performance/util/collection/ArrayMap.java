package com.fillumina.performance.util.collection;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

/**
 * Fast linked hash map implementation.
 * <ul>
 * <li>insertion, extraction and removal have O(1) complexity
 * <li>increases and decreases its size automatically
 * <li>maintains insertion order
 * <li>caches hash codes
 * <li>accesses its entries by index in O(1)
 * <li>views its keys as a list
 * <li>very fast to clone
 * <li>uses fast Cursor iteration
 * <li>doesn't accept null as key
 * <li>manages its own unmodifiable version of itself
 * <li>has copy constructor and clone constructor
 * <li>improves locality of access by using arrays
 * </ul>
 * {@link #Cursor} is faster than a standard iterator but is not
 * compliant with {@link Map} specifications because every {@link Map.Entry}
 * returned is in fact the same object.
 * <br>
 * Avoid using {@link #entrySet()} because to be compliant with the specs
 * it must create a new {@link Map.Entry} for each access.
 * Use {@link #iterator()} or {@link #cursor()} instead wich return a
 * {@link #Cursor}.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ArrayMap<K,V>
        implements Iterable<Entry<K,V>>, Map<K,V>, Cloneable, Serializable {

    public static final ArrayMap<?,?> EMPTY = new ArrayMap<>().unmodifiable();

    private static final long serialVersionUID = 1L;

    private Object[] array; // [key, value]
    private int[] indexes;  // [index, hashcode]
    private int size;       // actual size * 2

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

    public ArrayMap(Map<? extends K, ? extends V> copy) {
        putAll(copy);
    }

    public ArrayMap(ArrayMap<? extends K, ? extends V> clone) {
        if (clone.array != null) {
            this.array = clone.array.clone();
            this.indexes = clone.indexes.clone();
            this.size = clone.size;
        }
    }

    public ArrayMap(int initialSize) {
        if (initialSize < 0) {
            throw new IllegalArgumentException(
                    "initial size must be > 0, was " + initialSize);
        }
        int length = roundUpToPowerOf2(initialSize);
        this.array = new Object[length];
        this.indexes = new int[length];
    }

    private static int roundUpToPowerOf2(int number) {
        // assert number >= 0 : "number must be non-negative";
        return (number > 1) ? Integer.highestOneBit((number - 1) << 1) : 2; //1;
    }


    protected ArrayMap(Object[] array, int[] hashes, int size) {
        this.array = array;
        this.indexes = hashes;
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
                super.indexes = ArrayMap.this.indexes;
                super.size = ArrayMap.this.size;
            }
        }

        @Override
        public boolean isUnmodifiable() {
            return true;
        }

        @Override
        public V get(Object key) {
            relink();
            return super.get(key);
        }

        @Override
        public int size() {
            relink();
            return super.size();
        }

        @Override
        public boolean containsValue(Object value) {
            relink();
            return super.containsValue(value);
        }

        @Override
        public boolean containsKey(Object key) {
            relink();
            return super.containsKey(key);
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

    public ArrayMap<K,V> unmodifiable() {
        if (isUnmodifiable()) {
            return this;
        }
        if (unmodifiableView == null) {
            unmodifiableView = new UnmodifiableView(array, indexes, size);
        }
        return unmodifiableView;
    }

    @Override
    public ArrayMap<K,V> clone() throws CloneNotSupportedException {
        if (array == null) {
            return new ArrayMap<>();
        }
        return new ArrayMap<>(this);
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
        if (array == null) {
            array = new Object[8];
            indexes = new int[8];
        } else if (size > (indexes.length >> 1)) {
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
                indexes[bucket + 1] = size + 1; // points to value
                array[size] = key;
                array[size + 1] = value;
                size += 2;
                return null;
            }
            if (hashcode == indexes[bucket]) {
                if (key.equals(array[pointer - 1])) {
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
        ArrayMap<K,V> newMap = new ArrayMap<>(newSize);
        for (int i=0; i<size; i+=2) {
            newMap.put((K)array[i], (V)array[i+1]);
        }
        this.array = newMap.array;
        this.indexes = newMap.indexes;
    }

    @Override
    @SuppressWarnings("unchecked")
    public V get(Object key) {
        if (array == null || size == 0) {
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
                if (key.equals(array[pointer - 1])) {
                    return (V) array[pointer];
                }
            }
            bucket = (bucket + 2) & mask;
        }
    }

    @Override
    public boolean containsKey(Object key) {
        if (array == null || size == 0) {
            return false;
        }
        int mask = getMask();
        int hashcode = key.hashCode();
        int bucket = hashcode & mask;
        while (true) {
            int pointer = indexes[bucket + 1];
            if (pointer == 0) {
                return false; // not found
            }
            if (hashcode == indexes[bucket]) {
                if (key.equals(array[pointer - 1])) {
                    return true;
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
                if (key.equals(array[pointer - 1])) {
                    int index = pointer - 1;
                    @SuppressWarnings("unchecked")
                    V oldValue = (V) array[pointer];
                    System.arraycopy(array, index + 2,
                            array, index, array.length - index - 2);
                    indexes[bucket + 1] = 0; // free the bucket

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
                        if (b != w && indexes[w + 1] == 0) {
                            // relocate the bucket
                            indexes[w] = indexes[b];
                            indexes[w + 1] = indexes[b + 1];
                            indexes[b + 1] = 0; // free the former bucket
                        }
                    }
                    size -= 2;
                    if (size < (indexes.length >> 2)) {
                        resize(indexes.length >> 1);
                    }
                    return oldValue;
                }
            }
            bucket = (bucket + 2) & mask;
        }
    }

    protected void removeIndex(int index) {
        if (array == null || index < 0 || index > size) {
            throw new IllegalStateException();
        }
        remove(array[index]);
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
        indexes = null;
        array = null;
        size = 0;
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
            return ArrayMap.this.get(key);
        }

        @Override
        public V setValue(V value) {
            return ArrayMap.this.put(key, value);
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
            return key + " = " + Objects.toString(ArrayMap.this.get(key));
        }
    }

    /** WARNING! this is a <b>mutable</b> object! */
    public class Cursor implements Entry<K,V>, Iterator<Entry<K,V>> {
        private int index = -2;
        private boolean removed;

        public Cursor() {}

        public Cursor(int index) {
            this.index = index;
        }

        private Entry<K,V> createEntry() {
            return new EntryImpl(getKey());
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
        public Cursor next() {
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
            return "" + index + ": " +
                    getKey().toString() + " = " + Objects.toString(getValue());
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
    /**
     * WARNING: using this set means that a new {@link Map.Entry} must be
     * created at each access.
     */
    public Set<Entry<K, V>> entrySet() {
        if (entrySet == null) {
            entrySet = new EntrySet();
        }
        return entrySet;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        for (int i=0; i<size; i+=2) {
            hash = 31 * hash + array[i].hashCode(); // cannot be null
            hash = 31 * hash + Objects.hashCode(array[i + 1]);
        }
        return hash;
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
            Iterator<Entry<K,V>> i = iterator();
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
