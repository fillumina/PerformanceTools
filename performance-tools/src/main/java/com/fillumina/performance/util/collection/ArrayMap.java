package com.fillumina.performance.util.collection;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Fast hash map implementation.
 * Features:
 * <ul>
 * <li>insertion, extraction and removal have O(1) complexity
 * <li>linear for worst case (colliding hash) O(N)
 * <li>increases and decreases its size automatically
 * <li>very fast to clone
 * <li>uses fast Cursor iteration
 * <li>manages its own unmodifiable version of itself
 * <li>has copy constructor and clone constructor
 * <li>improves locality of access by using arrays
 * <li>use less memory by avoiding creating Entry objects
 * </ul>
 * Drawbacks:
 * <ul>
 * <li>doesn't accept null as key
 * <li>doesn't cache hashes so key.hashcode() must be fast.
 * <li>doesn't work too well for huge size
 * </ul>
 * {@link #Cursor} is faster than a standard iterator but it is not
 * compliant with {@link Map} specifications because every {@link Map.Entry}
 * returned is in fact the same object.
 * <br>
 * NOTICE:
 * Avoid using {@link #entrySet()} because to be compliant with the {@link Map}
 * specs it must create a new {@link Map.Entry} at each entry access.
 * Use map's {@link #iterator()} or {@link #cursor()} instead which return a
 * {@link #Cursor}.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ArrayMap<K,V>
        implements Iterable<Entry<K,V>>, Map<K,V>, Cloneable, Serializable {

    public static final ArrayMap<?,?> EMPTY =
            new ArrayMap<>().unmodifiable();

    private static final long serialVersionUID = 1L;

    private Object[] array; // [key, value]
    private int size;       // actual size

    private transient Set<Entry<K,V>> entrySet;
    private transient Set<K> keySet;
    private transient Collection<V> values;
    private transient ArrayMap<K,V> unmodifiableView;

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
            this.size = clone.size;
        }
    }

    public ArrayMap(int initialSize) {
        if (initialSize < 0) {
            throw new IllegalArgumentException(
                    "initial size must be > 0, was " + initialSize);
        }
        int length = roundUpToPowerOf2(initialSize);
        this.array = new Object[length << 2];
    }

    protected ArrayMap(ArrayMap<K,V> delegate, boolean notUsed) {
        setDelegate(delegate);
    }

    protected void setDelegate(ArrayMap<K,V> delegate) {
        this.array = delegate.array;
        this.size = delegate.size;
    }

    private static int roundUpToPowerOf2(int number) {
        return (number > 1) ? Integer.highestOneBit((number - 1) << 1) : 2; //1;
    }

    @SuppressWarnings("unchecked")
    public static <K,V> ArrayMap<K,V> create(Object... objects) {
        final ArrayMap<K,V> map = new ArrayMap<>(objects.length >> 1);
        for (int i=0; i<objects.length; i+=2) {
            map.put((K) objects[i], (V) objects[i+1]);
        }
        return map;
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

    public static class UnmodifiableView<K,V> extends ArrayMap<K,V> {
        private static final long serialVersionUID = 1L;

        UnmodifiableView(ArrayMap<K,V> delegate) {
            super(delegate, true);
        }

        @Override
        public boolean isUnmodifiable() {
            return true;
        }

        @Override
        public void replaceAll(
                BiFunction<? super K, ? super V, ? extends V> function) {
            throw new UnsupportedOperationException();
        }

        @Override
        public V getOrSet(K key, Supplier<V> valueProducer) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void ensureCapacity(int requiredCapacity) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ArrayMap<K, V> clone() throws CloneNotSupportedException {
            return this;
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

        @Override
        public Cursor cursor() {
            return new Cursor(true);
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
            unmodifiableView = new UnmodifiableView<>(this);
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

    public void ensureCapacity(int requiredCapacity) {
        int available = (array == null) ? 0 : ((array.length >> 2) - size);
        if (requiredCapacity > available) {
            int newSize = roundUpToPowerOf2(size + requiredCapacity);
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
    public ArrayMap<K,V> add(K key, V value) {
        put(key, value);
        return this;
    }

    @Override
    public V put(K key, V value) {
        if (array == null) {
            array = new Object[16];
        } else if (size == (array.length >> 1)) {
            resize(array.length << 1);
        }
        int mask = getMask();
        int bucket = hash(key) & mask;
        while (true) {
            Object k = array[bucket];
            if (k == null) {
                // bucket free, use it
                array[bucket] = key;
                array[bucket + 1] = value;
                size++;
                if (unmodifiableView != null) {
                    unmodifiableView.size = size;
                }
                return null;
            }
            if (equals(key, k)) {
                // bucket used, right key, set value
                @SuppressWarnings("unchecked")
                V oldValue = (V) array[bucket + 1];
                array[bucket + 1] = value;
                return oldValue;
            }
            bucket = (bucket + 2) & mask;
        }
    }

    private int getMask() {
        return (array.length - 1) & (~1);
    }

    @SuppressWarnings("unchecked")
    private void resize(int newSize) {
        if (newSize == 0) {
            array = null;
        } else if (array == null) {
            array = new Object[newSize << 2];
        } else {
            ArrayMap<K,V> newMap = new ArrayMap<>(newSize);
            for (int i=0,l=array.length; i<l; i+=2) {
                K k = (K) array[i];
                if (k != null) {
                    newMap.put(k, (V)array[i+1]);
                }
            }
            this.array = newMap.array;
        }
        if (unmodifiableView != null) {
            unmodifiableView.setDelegate(this);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public V get(Object key) {
        if (array == null || size == 0) {
            return null;
        }
        int mask = getMask();
        int bucket = hash(key) & mask;
        while (true) {
            Object k = array[bucket];
            if (k == null) {
                return null; // not found
            }
            if (equals(key, k)) {
                return (V) array[bucket + 1];
            }
            bucket = (bucket + 2) & mask;
        }
    }

    public V getOrSet(K key, Supplier<V> valueProducer) {
        V v = get(key);
        if (v == null) {
            v = valueProducer.get();
            put(key, v);
        }
        return v;
    }

    @Override
    public boolean containsKey(Object key) {
        return get(key) != null;
    }

    @Override
    public V remove(Object key) {
        if (array == null) {
            return null;
        }
        int mask = getMask();
        int bucket = hash(key) & mask;
        while (true) {
            Object k = array[bucket];
            if (k == null) {
                return null; // not found
            }
            if (equals(key, k)) {
                @SuppressWarnings("unchecked")
                V oldValue = (V) array[bucket + 1];
                array[bucket + 1] = null;
                array[bucket] = null;

                // reposition subsequent buckets
                while (true) {
                    bucket = (bucket + 2) & mask;
                    k = array[bucket];
                    if (k == null) {
                        break;
                    }
                    int w = hash(k) & mask; // where it want to be
                    if (w != bucket) {
                        while (true) {
                            if (array[w] == null) {
                                array[w] = array[bucket];
                                array[bucket] = null;
                                array[w + 1] = array[bucket + 1];
                                array[bucket + 1] = null;
                                break;
                            }
                            w = (w + 2) & mask;
                        }
                    }
                }

                size--;
                if (size < (array.length >> 3)) {
                    resize(array.length >> 3);
                } else if (unmodifiableView != null) {
                    unmodifiableView.size = size;
                }
                return oldValue;
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
        for (int i=1,l=array.length; i<l; i+=2) {
            @SuppressWarnings("unchecked")
            V v = (V) array[i];
            if (array[i-1] != null && equals(value, v)) {
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
            size = 0;
            if (unmodifiableView != null) {
                unmodifiableView.size = size;
            }
        }
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
        private final boolean unmodifiable;
        private int index = -2;
        private int next = getNext();
        private boolean removed;

        public Cursor(boolean unmodifiable) {
            this.unmodifiable = unmodifiable;
        }

        private Entry<K,V> createEntry() {
            return new EntryImpl(getKey());
        }

        public boolean moveForward(int step) {
            for (int i=0; i<step; i++) {
                if (hasNext()) {
                    next();
                } else {
                    return false;
                }
            }
            return true;
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
            if (unmodifiable) {
                throw new UnsupportedOperationException("not supported");
            }
            @SuppressWarnings("unchecked")
            V v = (V) array[index + 1];
            array[index + 1] = value;
            return v;
        }

        @Override
        public boolean hasNext() {
            return next != -1;
        }

        @Override
        public Cursor next() {
            index = next;
            removed = false;
            next = getNext();
            return this;
        }

        private int getNext() {
            if (array == null) {
                return -1;
            }
            for(int i=index+2,l=array.length; i<l; i+=2) {
                if (array[i] != null) {
                    return i;
                }
            }
            return -1;
        }

        @Override
        public void remove() {
            if (unmodifiable) {
                throw new UnsupportedOperationException("not supported");
            }
            if (array == null || index < 0) {
                throw new IllegalStateException();
            }
            @SuppressWarnings("unchecked")
            K k = (K) array[index];
            if (removed || k == null) {
                throw new IllegalStateException();
            }
            ArrayMap.this.remove(k);
            removed = true;
            index -= 2;
            next = getNext();
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
        return new Cursor(isUnmodifiable());
    }

    @Override
    public Iterator<Entry<K, V>> iterator() {
        return cursor();
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
            return size;
        }
    }

    private class EntrySet extends View<Entry<K,V>> {
        @Override
        Entry<K, V> select(Cursor entry) {
            return entry.createEntry();
        }

        @Override
        public void forEach(Consumer<? super Entry<K, V>> action) {
            Cursor c = new Cursor(true);
            while (c.hasNext()) {
                action.accept(c.next());
            }
        }

        @Override
        public Spliterator<Entry<K, V>> spliterator() {
            return new Spliterator<Entry<K,V>>() {
                private Cursor c = new Cursor(true);

                @Override
                public boolean tryAdvance(
                        Consumer<? super Entry<K, V>> action) {
                    if (c.hasNext()) {
                        action.accept(c.next());
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
        for (int i=0; i<size; i+=2) {
            @SuppressWarnings("unchecked")
            K k = (K)array[i];
            if (k != null) {
                hash = 31 * hash + array[i].hashCode();
                hash = 31 * hash + Objects.hashCode(array[i + 1]);
            }
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

        return true;
    }

    @Override
    public String toString() {
        Cursor i = new Cursor(true);
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
