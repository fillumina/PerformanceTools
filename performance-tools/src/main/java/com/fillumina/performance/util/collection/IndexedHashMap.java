package com.fillumina.performance.util.collection;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Indexed hash map implementation (similar in features to
 * {@link LinkedHashMap} but can index its entries randomly).
 * It allows to extract a list view of its keys and values but is has very
 * slow removal time.
 * Features:
 * <ul>
 * <li>insertion, extraction have O(1) complexity
 * <li>worse case (hash clash) for insertion and extraction is linear O(N)
 * <li>removal is linear O(N) VERY INEFFICIENT
 * <li>maintains insertion order
 * <li>views are random access list
 * <li>manages its own very efficient unmodifiable view of itself
 * <li>has copy constructor and clone constructor
 * <li>improves locality of access by using arrays
 * </ul>
 * Drawbacks:
 * <ul>
 * <li>doesn't accept null as key
 * <li>removal time is linear O(N)
 * </ul>
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class IndexedHashMap<K,V>
        implements Iterable<Entry<K,V>>, Map<K,V>, Cloneable, Serializable {

    public static final IndexedHashMap<?,?> EMPTY =
            new IndexedHashMap<>().unmodifiableView();

    private static final long serialVersionUID = 1L;

    private EntryImpl<K,V>[] array;
    private int[] indexes;
    private int size;
    private boolean readonly;

    private transient EntrySet entrySet;
    private transient KeySet keySet;
    private transient Values values;
    private transient IndexedHashMap<K,V> unmodifiableView;

    @SuppressWarnings("unchecked")
    public static <K,V> IndexedHashMap<K,V> emtpy() {
        return (IndexedHashMap<K, V>) EMPTY;
    }

    public IndexedHashMap() {
    }

    public IndexedHashMap(Map<? extends K, ? extends V> copy) {
        putAll(copy);
    }

    @SuppressWarnings("unchecked")
    public IndexedHashMap(IndexedHashMap<? extends K, ? extends V> clone) {
        if (clone.array != null) {
            this.indexes = clone.indexes.clone();
            this.size = clone.size;
            this.array = new EntryImpl[clone.array.length];
            EntryImpl<K,V> c[] = (EntryImpl<K,V>[]) clone.array;
            for (int i=0, l=size; i<l; i++) {
                this.array[i] = c[i].clone();
            }
        }
    }

    @SuppressWarnings("unchecked")
    public IndexedHashMap(int initialSize) {
        if (initialSize < 0) {
            throw new IllegalArgumentException(
                    "initial size must be > 0, was " + initialSize);
        }
        int length = roundUpToPowerOf2(initialSize);
        this.array = new EntryImpl[length];
        this.indexes = new int[length << 1];
    }

    private static int roundUpToPowerOf2(int number) {
        // assert number >= 0 : "number must be non-negative";
        return (number > 1) ? Integer.highestOneBit((number - 1) << 1) : 2; //1;
    }

    /** Special constructor to use to create unmodifiable view. */
    protected IndexedHashMap(IndexedHashMap<K,V> delegate, boolean readonly) {
        if (!readonly) {
            // check that it is used only to create the unmodifiable view
            throw new IllegalArgumentException("readonly must be true");
        }
        setDelegate(delegate);
        this.readonly = readonly;
    }

    protected void setDelegate(IndexedHashMap<K, V> delegate) {
        this.array = delegate.array;
        this.indexes = delegate.indexes;
        this.size = delegate.size;
    }

    /**
     * Builder to create a map from key,value pairs.
     *
     * @param objects pairs of key, value
     * @return the map
     */
    @SuppressWarnings("unchecked")
    public static <K,V> IndexedHashMap<K,V> create(Object... objects) {
        final IndexedHashMap<K,V> map = new IndexedHashMap<>();
        for (int i=0; i<objects.length; i+=2) {
            map.put((K) objects[i], (V) objects[i+1]);
        }
        return map;
    }

    /** Converts to another map. */
    public <W> IndexedHashMap<K,W> transform(Function<V,W> converter) {
        IndexedHashMap<K,W> map = new IndexedHashMap<>();
        for (Map.Entry<K,V> t : this) {
            map.put(t.getKey(), converter.apply(t.getValue()));
        }
        return map;
    }

    @SuppressWarnings("unchecked")
    public K getKeyAtIndex(int index) {
        return array[index].getKey();
    }

    @SuppressWarnings("unchecked")
    public V getValueAtIndex(int index) {
        return array[index].getValue();
    }

    @SuppressWarnings("unchecked")
    public V setValueAtIndex(int index, V value) {
        return array[index].setValue(value);
    }

    public Entry<K,V> getEntryAtIndex(int index) {
        return array[index];
    }

    public void removeEntryAtIndex(int index) {
        if (index < 0 || index > size) {
            throw new IllegalStateException();
        }
        remove(getKeyAtIndex(index));
    }

    /**
     * Override if you need a different equals().
     * @param a the given object
     * @param b the internal key
     */
    protected boolean equalsKey(Object a, Object b) {
        return Objects.equals(a, b);
    }

    /**
     * Override if you need a different hash function.
     */
    protected int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 15);
    }

    /** @return an unmodifiable clone. */
    public IndexedHashMap<K,V> unmodifiableClone() {
        return clone().unmodifiableView();
    }

    public boolean isUnmodifiable() {
        return readonly;
    }

    public IndexedHashMap<K,V> unmodifiableView() {
        if (unmodifiableView == null) {
            unmodifiableView = createUnmodifiable();
        }
        return unmodifiableView;
    }

    protected IndexedHashMap<K, V> createUnmodifiable() {
        return new IndexedHashMap<>(this, true);
    }

    @Override
    public IndexedHashMap<K,V> clone() {
        if (array == null) {
            return new IndexedHashMap<>();
        }
        return new IndexedHashMap<>(this);
    }

    public void ensureCapacity(int requiredCapacity) {
        if (readonly) {
            return;
        }
        int available = (array == null) ? 0 : ((array.length - size) >> 1);
        if (requiredCapacity > available) {
            int newSize = roundUpToPowerOf2((size >> 1) + requiredCapacity);
            resize(newSize);
        }
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    /** Useful with fluid interface initializations. */
    public IndexedHashMap<K,V> add(K key, V value) {
        put(key, value);
        return this;
    }

    @Override
    public V put(K key, V value) {
        if (readonly) {
            throw new UnsupportedOperationException("Not supported.");
        }
        if (array == null) {
            resize(8);
        } else if (size == array.length) {
            resize(size << 1);
        }
        int mask = getMask();
        int bucket = hash(key) & mask;
        while (true) {
            int pointer = indexes[bucket];
            if (pointer == 0) {
                // bucket free, use it
                array[size] = new EntryImpl<>(key, value);
                size++;
                indexes[bucket] = size;
                if (unmodifiableView != null) {
                    unmodifiableView.size = size;
                }
                return null;
            }
            EntryImpl<K,V> e = array[pointer - 1];
            if (equalsKey(key, e.key)) {
                // bucket used, right key, set value
                return e.setValue(value);
            }
            bucket = (bucket + 1) & mask;
        }
    }

    private int getMask() {
        return indexes.length - 1;
    }

    @SuppressWarnings("unchecked")
    private void resize(int newSize) {
        resize(newSize, -1);
    }

    @SuppressWarnings("unchecked")
    private void resize(int newSize, int butIndex) {
        if (newSize == 0) {
            this.array = null;
            this.indexes = null;
            this.size = 0;
        } else if (array == null) {
            this.array = new EntryImpl[newSize];
            this.indexes = new int[newSize << 1];
        } else {
            IndexedHashMap<K,V> newMap = new IndexedHashMap<>(newSize);
            for (int i=0; i<size; i++) {
                if (i != butIndex) {
                    EntryImpl<K,V> e = array[i];
                    newMap.put(e.key, e.value);
                }
            }
            this.array = newMap.array;
            this.indexes = newMap.indexes;
        }
        if (unmodifiableView != null) {
            unmodifiableView.setDelegate(this);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public V get(Object key) {
        Entry<K,V> result = getEntry((K) key);
        return result == null ? null : result.getValue();
    }

    public Entry<K,V> getEntry(K key) {
        if (size == 0) {
            return null;
        }
        int mask = getMask();
        int bucket = hash(key) & mask;
        while (true) {
            int pointer = indexes[bucket];
            if (pointer == 0) {
                return null; // not found
            }
            EntryImpl<K,V> e = array[pointer - 1];
            if (equalsKey(key, e.key)) {
                return e;
            }
            bucket = (bucket + 1) & mask;
        }
    }

    @Override
    public boolean containsKey(Object key) {
        return getIndexOfKey(key) != -1;
    }

    public int getIndexOfKey(Object key) {
        if (size == 0) {
            return -1;
        }
        int mask = getMask();
        int bucket = hash(key) & mask;
        while (true) {
            int pointer = indexes[bucket];
            if (pointer == 0) {
                return -1; // not found
            }
            Entry<K,V> e = array[pointer - 1];
            if (equalsKey(key, e.getKey())) {
                return pointer - 1;
            }
            bucket = (bucket + 1) & mask;
        }
    }

    /** WARNING: inefficient method O(N) */
    @Override
    public V remove(Object key) {
        if (readonly) {
            throw new UnsupportedOperationException("Not supported.");
        }
        if (array == null) {
            return null;
        }
        int mask = getMask();
        int bucket = hash(key) & mask;
        while (true) {
            int pointer = indexes[bucket];
            if (pointer == 0) {
                return null; // not found
            }
            EntryImpl<K,V> e = array[pointer - 1];
            if (equalsKey(key, e.key)) {
                V oldValue = e.value;

                removeIndex(pointer - 1, bucket, mask);
                if (unmodifiableView != null) {
                    unmodifiableView.size = size;
                }
                return oldValue;
            }
            bucket = (bucket + 1) & mask;
        }
    }

    private void removeIndex(int index, int bucket, int mask) {
        size--;

        // shift the array list from index back by 1
        System.arraycopy(array, index + 1,
                array, index, array.length - index - 1);
        // clear last positions to let GC do its work
        if (array.length == size) {
            array[array.length] = null;
        }

        // free the bucket
        indexes[bucket] = 0;

        // adjusts indexes (O(N))
        for (int i=0,l=indexes.length; i<l; i++) {
            int idx = indexes[i];
            if (idx > index) {
                indexes[i] = idx - 1;
            }
        }

        // rolls subsequent buckets back if needed
        int b = bucket;
        while (true) {
            b = (b + 1) & mask;
            int p = indexes[b];
            if (p == 0) {
                break;
            }
            int h = hash(array[p - 1].key);
            int w = h & mask; // where it want to be
            if (b != w) {
                while (indexes[w] != 0) {
                    w = (w + 1) & mask;
                }
                // relocate the bucket
                indexes[w] = indexes[b];
                indexes[b] = 0; // free the former bucket
            }
        }
    }

    @Override
    public boolean containsValue(Object value) {
        if (array == null) {
            return false;
        }
        for (int i=0,l=size; i<l; i++) {
            EntryImpl<K,V> e = array[i];
            if (e != null && Objects.equals(e.value, value)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        if (readonly) {
            throw new UnsupportedOperationException("Not supported.");
        }
        ensureCapacity(m.size());
        m.forEach((k,v) -> put(k,v));
    }

    @Override
    public void clear() {
        if (readonly) {
            throw new UnsupportedOperationException("Not supported.");
        }
        if (array != null) {
            Arrays.fill(array, null);
            Arrays.fill(indexes, 0);
            size = 0;
        }
        if (unmodifiableView != null) {
            unmodifiableView.size = size;
        }
    }

    private static class UnmodifiableEntryImpl<K,V> implements Map.Entry<K,V> {
        private final EntryImpl<K,V> delegate;

        public UnmodifiableEntryImpl(EntryImpl<K, V> delegate) {
            this.delegate = delegate;
        }

        @Override
        public K getKey() {
            return delegate.key;
        }

        @Override
        public V getValue() {
            return delegate.value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public int hashCode() {
            // as per definition of hashCode
            return getKey().hashCode();
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
            if (!Objects.equals(getKey(), other.getKey())) {
                return false;
            }
            return Objects.equals(getValue(), other.getValue());
        }
    }

    public static class EntryImpl<K,V> implements Map.Entry<K,V>, Cloneable {
        private final K key;
        private V value;
        private UnmodifiableEntryImpl<K,V> uentry;

        public EntryImpl(K key, V value) {
            this.key = key;
            this.value = value;
        }

        Entry<K,V> unmodifiable() {
            if (uentry == null) {
                uentry = new UnmodifiableEntryImpl<>(this);
            }
            return uentry;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            V oldValue = this.value;
            this.value = value;
            return oldValue;
        }

        @Override
        protected EntryImpl<K,V> clone() {
            return new EntryImpl<>(key, value);
        }

        @Override
        public int hashCode() {
            // as per definition of hashCode
            return key.hashCode();
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
            return Objects.equals(this.value, other.getValue());
        }

        @Override
        public String toString() {
            return this.key + " = " + Objects.toString(this.value);
        }
    }

    public class ListIteratorImpl implements ListIterator<Entry<K,V>> {
        private final int start;
        private int end;
        private int index;
        private boolean removed;

        public ListIteratorImpl() {
            this(0, IndexedHashMap.this.size());
        }

        public ListIteratorImpl(int index) {
            this();
            this.index = index;
        }

        public ListIteratorImpl(int start, int end) {
            this.start = start;
            this.end = end;
            this.index = start - 1;
        }

        public void setIndex(int index) {
            if (index < start || index > end) {
                throw new IndexOutOfBoundsException("index=" + index);
            }
            this.index = index;
        }

        @Override
        public boolean hasNext() {
            return index + 1 < end;
        }

        @Override
        public Entry<K,V> next() {
            removed = false;
            index++;
            return array[index];
        }

        @Override
        public boolean hasPrevious() {
            return index - 1 >= start;
        }

        @Override
        public Entry<K, V> previous() {
            removed = false;
            index--;
            return array[index];
        }

        @Override
        public void remove() {
            if (readonly) {
                throw new UnsupportedOperationException("Not supported.");
            }
            if (removed) {
                throw new IllegalStateException();
            }
            removeEntryAtIndex(index);
            index--;
            end--;
            removed = true;
        }

        @Override
        public int nextIndex() {
            return index + 1;
        }

        @Override
        public int previousIndex() {
            return index - 1;
        }

        @Override
        public void set(Entry<K, V> e) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public void add(Entry<K, V> e) {
            IndexedHashMap.this.put(e.getKey(), e.getValue());
        }
    }

    /** Uses fast cursor. */
    @Override
    public Iterator<Entry<K, V>> iterator() {
        return new ListIteratorImpl();
    }

    private abstract class View<T> extends AbstractSet<T>
            implements List<T>, Serializable {
        private static final long serialVersionUID = 1L;
        protected int start, end;

        public View() {
            this(0, IndexedHashMap.this.size());
        }

        public View(int start, int end) {
            if (start > end || start < 0 || end > IndexedHashMap.this.size()) {
                throw new IndexOutOfBoundsException("start=" + start +
                        ", end=" + end + ", size=" + IndexedHashMap.this.size());
            }
            this.start = start;
            this.end = end;
        }

        abstract T select(Entry<K,V> entry);

        protected void rangeCheck(int index) {
            if (index < start || index > end ) {
                throw new IndexOutOfBoundsException("start=" + start +
                        ", end=" + end + ", size=" + IndexedHashMap.this.size());
            }
        }

        @Override
        public Iterator<T> iterator() {
            return listIterator();
        }

        @Override
        public int size() {
            rangeCheck(start);
            return Math.min(end - start, IndexedHashMap.this.size());
        }

        @Override
        public void clear() {
            if (readonly) {
                throw new UnsupportedOperationException("Not supported.");
            }
            start = 0;
            end = 0;
            IndexedHashMap.this.clear();
        }

        @Override
        public Spliterator<T> spliterator() {
            return List.super.spliterator();
        }

        @Override
        public ListIterator<T> listIterator() {
            return listIterator(0);
        }

        @Override
        public ListIterator<T> listIterator(int index) {
            rangeCheck(index);
            return new ListIterator<T>() {
                private ListIteratorImpl c = new ListIteratorImpl(start + index, end);

                @Override
                public boolean hasNext() {
                    return c.hasNext();
                }

                @Override
                public T next() {
                    return select(c.next());
                }

                @Override
                public boolean hasPrevious() {
                    return c.hasPrevious();
                }

                @Override
                public T previous() {
                    return select(c.previous());
                }

                @Override
                public int nextIndex() {
                    return c.nextIndex();
                }

                @Override
                public int previousIndex() {
                    return c.previousIndex();
                }

                @Override
                public void remove() {
                    if (readonly) {
                        throw new UnsupportedOperationException("Not supported.");
                    }
                    c.remove();
                }

                @Override
                public void set(T e) {
                    throw new UnsupportedOperationException("Not supported");
                }

                @Override
                public void add(T e) {
                    throw new UnsupportedOperationException("Not supported.");
                }
            };
        }

        @Override
        public int lastIndexOf(Object o) {
            Iterator<T> c = listIterator();
            int index = 0;
            int result = -1;
            while (c.hasNext()) {
                if (Objects.equals(o, c.next())) {
                    result = index;
                }
            }
            return result;
        }

        @Override
        public int indexOf(Object o) {
            Iterator<T> c = listIterator();
            int index = 0;
            while (c.hasNext()) {
                if (Objects.equals(o, c.next())) {
                    return index;
                }
            }
            return -1;
        }

        @Override
        public T remove(int index) {
            if (readonly) {
                throw new UnsupportedOperationException("Not supported.");
            }
            rangeCheck(index);
            T t = get(index);
            IndexedHashMap.this.removeEntryAtIndex(start + index);
            return t;
        }

        @Override
        public T get(int index) {
            rangeCheck(index);
            Entry<K,V> entry = getEntryAtIndex(start + index);
            return select(entry);
        }

        @Override
        public T set(int index, T element) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(int index, T element) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(int index, Collection<? extends T> c) {
            throw new UnsupportedOperationException();
        }
    }

    private class EntrySet extends View<Entry<K,V>> {
        private static final long serialVersionUID = 1L;

        EntrySet() {
            super();
        }

        EntrySet(int start, int end) {
            super(start, end);
        }

        @Override
        Entry<K, V> select(Entry<K,V> entry) {
            if (readonly) {
                return ((EntryImpl<K,V>)entry).unmodifiable();
            }
            return entry;
        }

        @Override
        public List<Entry<K, V>> subList(int fromIndex, int toIndex) {
            rangeCheck(fromIndex);
            rangeCheck(toIndex);
            return new EntrySet(start + fromIndex, start + toIndex);
        }
    }

    private class KeySet extends View<K> {
        private static final long serialVersionUID = 1L;

        public KeySet() {
            super();
        }

        public KeySet(int start, int end) {
            super(start, end);
        }

        @Override
        K select(Entry<K,V> entry) {
            return entry.getKey();
        }

        @Override
        @SuppressWarnings("unchecked")
        public K get(int index) {
            rangeCheck(index);
            return getKeyAtIndex(start + index);
        }

        @Override
        public boolean contains(Object o) {
            return indexOf(o) != -1;
        }

        @Override
        public int lastIndexOf(Object o) {
            return indexOf(o); // there aren't repetitions (it's a key set)
        }

        /** Very fast: accesses index in O(1). */
        @Override
        public int indexOf(Object o) {
            final int idx = getIndexOfKey(o);
            return idx < start ? -1 : idx - start;
        }

        @Override
        public List<K> subList(int fromIndex, int toIndex) {
            rangeCheck(fromIndex);
            rangeCheck(toIndex);
            return new KeySet(start + fromIndex, start + toIndex);
        }
    }

    private class Values extends View<V> {
        private static final long serialVersionUID = 1L;

        public Values() {
            super();
        }

        public Values(int start, int end) {
            super(start, end);
        }

        @Override
        V select(Entry<K,V> entry) {
            return entry.getValue();
        }

        @Override
        public List<V> subList(int fromIndex, int toIndex) {
            rangeCheck(fromIndex);
            rangeCheck(toIndex);
            return new Values(start + fromIndex, start + toIndex);
        }
    }

    public List<K> keyList() {
        return (List<K>) keySet();
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
        Iterator<Entry<K,V>> it = iterator();
        while (it.hasNext()) {
            hash = 31 * hash + it.next().hashCode();
        }
        return hash;
    }

    @Override
    public void forEach(Consumer<? super Entry<K, V>> action) {
        final ListIteratorImpl c = new ListIteratorImpl();
        while (c.hasNext()) {
            action.accept(c.next());
        }
    }

    @Override
    public Spliterator<Entry<K, V>> spliterator() {
        return entrySet().spliterator();
    }

    @Override
    public void replaceAll(
            BiFunction<? super K, ? super V, ? extends V> function) {
        if (readonly) {
            throw new UnsupportedOperationException("Not supported.");
        }
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
        ListIteratorImpl i = new ListIteratorImpl();
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
