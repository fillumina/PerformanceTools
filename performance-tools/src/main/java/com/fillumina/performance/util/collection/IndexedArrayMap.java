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
 * slow removal.
 * Features:
 * <ul>
 * <li>insertion, extraction have O(1) complexity
 * <li>worse case is linear O(N)
 * <li>removal is linear O(N) VERY INEFFICIENT
 * <li>increases and decreases its size automatically
 * <li>maintains insertion order
 * <li>views are random access list
 * <li>very fast to clone
 * <li>uses fast Cursor iteration
 * <li>doesn't accept null as key
 * <li>manages its own unmodifiable version of itself
 * <li>has copy constructor and clone constructor
 * <li>improves locality of access by using arrays
 * </ul>
 * Drawbacks:
 * <ul>
 * <li>doesn't accept null as key
 * <li>removal time is linear O(N), very inefficient!
 * <li>if you want to support unmodifiable views on an extended class a
 *     dedicated unmodifiable view class MUST be created.
 * </ul>
 * {@link #Cursor} is faster than a standard iterator but it is not
 * compliant with {@link Map} specifications because every {@link Map.Entry}
 * returned is in fact the same object.
 * <br>
 * Avoid using {@link #entrySet()} because to be compliant with the specs
 * it must create a new {@link Map.Entry} for each access.
 * Use map's {@link #iterator()} or {@link #cursor()} instead which return a
 * faster {@link #Cursor}.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class IndexedArrayMap<K,V>
        implements Iterable<Entry<K,V>>, Map<K,V>, Cloneable, Serializable {

    public static final IndexedArrayMap<?,?> EMPTY =
            new IndexedArrayMap<>().unmodifiable();

    private static final long serialVersionUID = 1L;

    private Object[] array; // [key, value]
    private int[] indexes;  // [index]
    private int size;       // actual size * 2

    private transient EntrySet entrySet;
    private transient KeySet keySet;
    private transient Values values;
    private transient IndexedArrayMap<K,V> unmodifiableView;

    @SuppressWarnings("unchecked")
    public static <K,V> IndexedArrayMap<K,V> emtpy() {
        return (IndexedArrayMap<K, V>) EMPTY;
    }

    public IndexedArrayMap() {
    }

    public IndexedArrayMap(Map<? extends K, ? extends V> copy) {
        putAll(copy);
    }

    public IndexedArrayMap(IndexedArrayMap<? extends K, ? extends V> clone) {
        if (clone.array != null) {
            this.array = clone.array.clone();
            this.indexes = clone.indexes.clone();
            this.size = clone.size;
        }
    }

    public IndexedArrayMap(int initialSize) {
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

    protected IndexedArrayMap(IndexedArrayMap<K,V> delegate, boolean notUsed) {
        setDelegate(delegate);
    }

    protected void setDelegate(IndexedArrayMap<K, V> delegate) {
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
    public static <K,V> IndexedArrayMap<K,V> create(Object... objects) {
        final IndexedArrayMap<K,V> map = new IndexedArrayMap<>();
        for (int i=0; i<objects.length; i+=2) {
            map.put((K) objects[i], (V) objects[i+1]);
        }
        return map;
    }

    /** Converts to another map. */
    public <W> IndexedArrayMap<K,W> transform(Function<V,W> converter) {
        IndexedArrayMap<K,W> map = new IndexedArrayMap<>();
        for (Map.Entry<K,V> t : this) {
            map.put(t.getKey(), converter.apply(t.getValue()));
        }
        return map;
    }

    @SuppressWarnings("unchecked")
    public K getKeyAtIndex(int index) {
        return (K) array[index << 1];
    }

    @SuppressWarnings("unchecked")
    public V getValueAtIndex(int index) {
        return (V) array[(index << 1) + 1];
    }

    @SuppressWarnings("unchecked")
    public V setValueAtIndex(int index, V value) {
        final int idx = (index << 1) + 1;
        V oldValue = (V) array[idx];
        array[idx] = value;
        return oldValue;
    }

    /**
     * Override if you need a different equals().
     * @param a the given object
     * @param b the internal key
     */
    protected boolean equals(Object a, Object b) {
        return Objects.equals(a, b);
    }

    /**
     * Override if you need a different hash function.
     */
    protected int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 15);
    }

    public Cursor getEntryAtIndex(int index) {
        return new Cursor(index);
    }

    protected static class UnmodifiableView<K,V> extends IndexedArrayMap<K,V> {
        private static final long serialVersionUID = 1L;

        protected UnmodifiableView(IndexedArrayMap<K,V> delegate) {
            super(delegate, true);
        }

        @Override
        public boolean isUnmodifiable() {
            return true;
        }

        @Override
        public IndexedArrayMap<K,V> unmodifiable() {
            return this;
        }

        @Override
        public IndexedArrayMap<K, V> clone() {
            return this;
        }

        @Override
        public void ensureCapacity(int requiredCapacity) {
            throw new UnsupportedOperationException();
        }

        @Override
        protected void removeEntryAtIndex(int index) {
            throw new UnsupportedOperationException();
        }

        @Override
        public V setValueAtIndex(int index, V value) {
            throw new UnsupportedOperationException();
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

    public IndexedArrayMap<K,V> unmodifiable() {
        if (unmodifiableView == null) {
            unmodifiableView = createUnmodifiable();
        }
        return unmodifiableView;
    }

    protected IndexedArrayMap<K, V> createUnmodifiable() {
        return new UnmodifiableView<>(this);
    }

    @Override
    public IndexedArrayMap<K,V> clone() {
        if (array == null) {
            return new IndexedArrayMap<>();
        }
        return new IndexedArrayMap<>(this);
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
    public IndexedArrayMap<K,V> add(K key, V value) {
        put(key, value);
        return this;
    }

    @Override
    public V put(K key, V value) {
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
                indexes[bucket] = size + 1; // points to value
                array[size] = key;
                array[size + 1] = value;
                size += 2;
                if (unmodifiableView != null) {
                    unmodifiableView.size = size;
                }
                return null;
            }
            if (equals(key, array[pointer - 1])) {
                // bucket used, right key, set value
                @SuppressWarnings("unchecked")
                V oldValue = (V) array[pointer];
                array[pointer] = value;
                return oldValue;
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

    private void resize(int newSize, int butIndex) {
        if (newSize == 0) {
            this.array = null;
            this.indexes = null;
            this.size = 0;
        } else if (array == null) {
            this.array = new Object[newSize];
            this.indexes = new int[newSize];
        } else {
            IndexedArrayMap<K,V> newMap = new IndexedArrayMap<>(newSize);
            for (int i=0; i<size; i+=2) {
                if (i != butIndex) {
                    newMap.put((K)array[i], (V)array[i+1]);
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
            if (equals(key, array[pointer - 1])) {
                return (V) array[pointer];
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
            if (equals(key, array[pointer - 1])) {
                return (pointer - 1) >> 1;
            }
            bucket = (bucket + 1) & mask;
        }
    }

    /** WARNING: inefficient method O(N) */
    @Override
    public V remove(Object key) {
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
            if (equals(key, array[pointer - 1])) {
                int index = pointer - 1;
                @SuppressWarnings("unchecked")
                V oldValue = (V) array[pointer];

                removeIndex(index, bucket, mask);
                if (unmodifiableView != null) {
                    unmodifiableView.size = size;
                }
                return oldValue;
            }
            bucket = (bucket + 1) & mask;
        }
    }

    private void removeIndex(int index, int bucket, int mask) {
        size -= 2;
        if (size < (indexes.length >> 2)) {
            resize(indexes.length >> 1, index);

        } else {

            // shift the array list from index back by 2
            System.arraycopy(array, index + 2,
                    array, index, array.length - index - 2);
            // clear last positions to let GC do its work
            if (array.length == size) {
                array[array.length - 1] = null;
                array[array.length - 2] = null;
            }

            // free the bucket
            indexes[bucket] = 0;

            // adjusts indexes (O(N))
            for (int i=0,l=indexes.length; i<l; i++) {
                int idx = indexes[i];
                if (idx > index) {
                    indexes[i] = idx - 2;
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
                int h = hash(array[p - 1]);
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
    }

    protected void removeEntryAtIndex(int index) {
        if (array == null || index < 0 || index > size) {
            throw new IllegalStateException();
        }
        remove(getKeyAtIndex(index));
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
        if (array != null) {
            Arrays.fill(array, null);
            Arrays.fill(indexes, 0);
            size = 0;
        }
        if (unmodifiableView != null) {
            unmodifiableView.size = size;
        }
        if (entrySet != null) {
            entrySet.reset();
        }
    }

    private class EntryImpl implements Map.Entry<K,V> {
        private final K key;
        private V value;

        public EntryImpl(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            int index = getIndexOfKey(key);
            if (index == -1) {
                return value;
            }
            value = IndexedArrayMap.this.getValueAtIndex(index);
            return value;
        }

        @Override
        public V setValue(V value) {
            this.value = value;
            return IndexedArrayMap.this.put(key, value);
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
            return Objects.equals(getValue(), other.getValue());
        }

        @Override
        public String toString() {
            return getKey() + " = " + Objects.toString(getValue());
        }
    }

    /** WARNING! this is a <b>mutable</b> object! */
    public class Cursor implements Entry<K,V>, ListIterator<Entry<K,V>> {
        private final int start;
        private int end;
        private int index;
        private boolean removed;

        public Cursor() {
            this(0, IndexedArrayMap.this.size());
        }

        public Cursor(int index) {
            this();
            this.index = index;
        }

        public Cursor(int start, int end) {
            this.start = start;
            this.end = end;
            this.index = start - 1;
        }

        private Entry<K,V> createEntry() {
            return new EntryImpl(getKey(), getValue());
        }

        public void setIndex(int index) {
            this.index = index;
        }

        @Override
        @SuppressWarnings("unchecked")
        public K getKey() {
            return getKeyAtIndex(index);
        }

        @Override
        @SuppressWarnings("unchecked")
        public V getValue() {
            return getValueAtIndex(index);
        }

        @Override
        public V setValue(V value) {
            return setValueAtIndex(index, value);
        }

        @Override
        public boolean hasNext() {
            return index + 1 < end;
        }

        @Override
        public Cursor next() {
            removed = false;
            index++;
            return this;
        }

        @Override
        public boolean hasPrevious() {
            return index - 1 >= start;
        }

        @Override
        public Entry<K, V> previous() {
            removed = false;
            index--;
            return this;
        }

        @Override
        public void remove() {
            if (removed) {
                throw new IllegalStateException();
            }
            removeEntryAtIndex(index);
            index--;
            end--;
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
                    getKey().toString() + " => " + Objects.toString(getValue());
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
            IndexedArrayMap.this.put(e.getKey(), e.getValue());
        }
    }

    public Cursor cursor() {
        return new Cursor();
    }

    /** Uses fast cursor. */
    @Override
    public Iterator<Entry<K, V>> iterator() {
        return new Cursor();
    }

    private abstract class View<T> extends AbstractSet<T>
            implements List<T>, Serializable {
        private static final long serialVersionUID = 1L;
        protected int start, end;

        public View() {
            this(0, IndexedArrayMap.this.size());
        }

        public View(int start, int end) {
            if (start > end || start < 0 || end > IndexedArrayMap.this.size()) {
                throw new IndexOutOfBoundsException("start=" + start +
                        ", end=" + end + ", size=" + IndexedArrayMap.this.size());
            }
            this.start = start;
            this.end = end;
        }

        abstract T select(Cursor entry);

        protected void rangeCheck(int index) {
            if (index < start || index > end ) {
                throw new IndexOutOfBoundsException("start=" + start +
                        ", end=" + end + ", size=" + IndexedArrayMap.this.size());
            }
        }

        @Override
        public Iterator<T> iterator() {
            return listIterator();
        }

        @Override
        public int size() {
            rangeCheck(start);
            return Math.min(end - start, IndexedArrayMap.this.size());
        }

        @Override
        public void clear() {
            start = 0;
            end = 0;
            IndexedArrayMap.this.clear();
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
                private Cursor c = new Cursor(start + index, end);

                @Override
                public boolean hasNext() {
                    return c.hasNext();
                }

                @Override
                public T next() {
                    c.next();
                    return select(c);
                }

                @Override
                public boolean hasPrevious() {
                    return c.hasPrevious();
                }

                @Override
                public T previous() {
                    c.previous();
                    return select(c);
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
            rangeCheck(index);
            T t = get(index);
            IndexedArrayMap.this.removeEntryAtIndex(start + index);
            return t;
        }

        @Override
        public T get(int index) {
            rangeCheck(index);
            Cursor cursor = getEntryAtIndex(start + index);
            return select(cursor);
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
        private Entry<K,V>[] entries;

        EntrySet() {
            super();
        }

        EntrySet(int start, int end) {
            super(start, end);
        }

        void reset() {
            this.entries = null;
        }

        @Override
        Entry<K, V> select(Cursor entry) {
            return entry.createEntry();
        }

        @Override
        public List<Entry<K, V>> subList(int fromIndex, int toIndex) {
            rangeCheck(fromIndex);
            rangeCheck(toIndex);
            return new EntrySet(start + fromIndex, start + toIndex);
        }

        @Override
        public void forEach(Consumer<? super Entry<K, V>> action) {
            final Cursor c = new Cursor();
            while (c.hasNext()) {
                action.accept(c.next());
            }
        }

        @Override
        @SuppressWarnings("unchecked")
        public Spliterator<Entry<K, V>> spliterator() {
            if (entries == null) {
                entries = (Entry<K, V>[]) new Entry[size];
            } else if (entries.length > (size << 1)) {
                Entry<K,V>[] old = entries;
                entries = (Entry<K, V>[]) new Entry[size];
                System.arraycopy(old, 0, entries, 0, old.length);
            } else if (entries.length < (size >> 1)) {
                Entry<K,V>[] old = entries;
                entries = (Entry<K, V>[]) new Entry[size];
                System.arraycopy(old, 0, entries, 0, size);
            }
            return new Spliterator<Entry<K,V>>() {
                private final Cursor c = new Cursor();

                @Override
                public boolean tryAdvance(
                        Consumer<? super Entry<K, V>> action) {
                    if (c.hasNext()) {
                        c.next();
                        Entry<K,V> entry = entries[c.index];
                        if (entry == null ||
                                !Objects.equals(entry.getKey(), c.getKey())) {
                            entry = c.createEntry();
                            entries[c.index] = entry;
                        }
                        action.accept(entry);
                        return true;
                    }
                    return false;
                }

                @Override
                public Spliterator<Entry<K, V>> trySplit() {
                    return null;
                }

                @Override
                public long estimateSize() {
                    return size();
                }

                @Override
                public int characteristics() {
                    return Spliterator.ORDERED | Spliterator.DISTINCT |
                            Spliterator.NONNULL | Spliterator.SIZED;
                }
            };
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
        K select(Cursor entry) {
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
        V select(Cursor entry) {
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

    /**
     * entrySet().stream() has been optimized to use Cursor.
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
    public void forEach(Consumer<? super Entry<K, V>> action) {
        final Cursor c = new Cursor();
        while (c.hasNext()) {
            action.accept(c.next());
        }
    }

    @Override
    public Spliterator<Entry<K, V>> spliterator() {
        return entrySet().spliterator();
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
