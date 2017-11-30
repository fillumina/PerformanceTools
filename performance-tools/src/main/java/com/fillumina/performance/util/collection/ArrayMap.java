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
 * It's a map loaded over an array list. It's fast to clone and iterate over but
 * slow to get and insert. It's useful for very small maps for its relatively
 * small footprint and some useful methods.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ArrayMap<K,V>
        implements Iterable<Entry<K,V>>, Map<K,V>, Cloneable, Serializable {

    private static final long serialVersionUID = 1L;
    private Object[] array;
    private int size;

    public ArrayMap() {
    }

    private ArrayMap(Object[] array, int size) {
        this.array = array.clone();
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

    public Entry<K,V> getEntryAtIndex(int index) {
        return new Cursor(index << 1);
    }

    @Override
    public ArrayMap<K,V> clone() throws CloneNotSupportedException {
        return new ArrayMap<>(array, size);
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
        } else if (array.length < size + 1) {
            Object[] tmp = new Object[array.length << 1];
            System.arraycopy(array, 0, tmp, 0, size);
            array = tmp;
        }
        array[size] = key;
        array[size + 1] = value;
        size += 2;
        return null;
    }

    private int getIndexOf(Object key) {
        if (array == null) {
            return -1;
        }
        for (int i=0,l=size; i<l; i+=2) {
            @SuppressWarnings("unchecked")
            K k = (K) array[i];
            if (Objects.equals(k, key)) {
                return i;
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

    private void removeIndex(int index) {
        if (array == null || index < 0 || index > size) {
            throw new IllegalStateException();
        }
        System.arraycopy(array, index + 2, array, index, array.length - index - 2);
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
        array = null;
        size = 0;
    }

    private class Cursor implements Entry<K,V>, Iterator<Entry<K,V>> {
        private int index = -2;
        private boolean removed;

        public Cursor() {}

        public Cursor(int index) {
            this.index = index;
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

    private Set<K> keySet;
    private List<K> keyList;
    private Collection<V> values;
    private Set<Entry<K,V>> entrySet;

    private abstract class View<T> extends AbstractSet<T> {
        abstract T select(Entry<K,V> entry);

        @Override
        public Iterator<T> iterator() {
            return new Iterator<T>() {
                private final Cursor cursor = new Cursor();

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

    private class KeySet extends View<K> {
        @Override K select(Entry<K, V> entry) { return entry.getKey(); }
    }

    private class EntrySet extends View<Entry<K,V>> {
        @Override Entry<K, V> select(Entry<K, V> entry) { return entry; }
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
