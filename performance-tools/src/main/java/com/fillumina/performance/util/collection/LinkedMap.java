package com.fillumina.performance.util.collection;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A {@link Map} based on a single linked list of entries.
 * It's quite slow compared to the default {@link Map} implementation.
 * <p>
 * It has some enhanced features:
 * <ul>
 * <li>it allows to insert an implementation of {@link LinkedEntry}
 * (the given entry value will be copied in the already mapped entry if present);
 * <li>{@link #getEntryAtIndex(int)} get the entry at the given position;
 * <li>{@link #getEntryWithKey(Object)} get the entry mapped with the given key;
 * <li>it has a very handy static creator {@link #create(java.lang.Object...) }.
 * </ul>
 * <p>
 * This class is not thread safe.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class LinkedMap<K,V>
        implements Iterable<Entry<K,V>>, Map<K,V>, Serializable {
    private static final long serialVersionUID = 1L;

    public static final LinkedMap<?,?> EMPTY =
            new LinkedMap<>().getUnmodifiableCopy();

    @SuppressWarnings("unchecked")
    public static <K,V> LinkedMap<K,V> empty() {
        return (LinkedMap<K, V>) EMPTY;
    }

    public static class MapBuilder<K,V> {
        private final LinkedMap<K,V> map = new LinkedMap<>();

        public MapBuilder<K,V> put(K key, V value) {
            map.put(key, value);
            return this;
        }

        public LinkedMap<K,V> build() {
            return new LinkedMap<>(map);
        }

        public LinkedMap<K,V> get() {
            return map;
        }
    }

    public static <K,V> MapBuilder<K,V> builder() {
        return new MapBuilder<>();
    }

    public static interface LinkedEntry<K,V> extends Entry<K,V> {
        LinkedEntry<K,V> getNext();
        void setNext(LinkedEntry<K,V> entry);
    }

    public static class LEntry<K,V> implements LinkedEntry<K,V> {
        private final K key;
        private V value;
        private LinkedEntry<K,V> next;

        public LEntry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public LinkedEntry<K, V> getNext() {
            return next;
        }

        @Override
        public void setNext(LinkedEntry<K, V> next) {
            this.next = next;
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
        public int hashCode() {
            int hash = 5;
            hash = 89 * hash + Objects.hashCode(this.key);
            hash = 89 * hash + Objects.hashCode(this.value);
            return hash;
        }

        // allows to be compared to whatever implementation of Map.
        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (!(obj instanceof Map.Entry)) {
                return false;
            }
            final Map.Entry<?, ?> other = (Map.Entry<?, ?>) obj;
            return Objects.equals(this.key, other.getKey()) &&
                Objects.equals(this.value, other.getValue());
        }
    }

    /** Faster insertions but iterates with reverse order of insertion. */
    public static class Inverse<K,V> extends LinkedMap<K,V> {
        private static final long serialVersionUID = 1L;

        public Inverse() {
        }

        public Inverse(Map<? extends K, ? extends V> copy) {
            super(copy);
        }

        public Inverse(Iterable<Entry<? extends K, ? extends V>> copy) {
            super(copy);
        }

        @Override
        protected void linkEntry(LinkedEntry<K, V> entry) {
            addAtBeginning(entry);
        }

    }

    /**
     * CAUTION: this method block static checking!
     * @param <K>
     * @param <V>
     * @param objects
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <K,V> LinkedMap<K,V> createCheck(
            Class<K> keyClass, Class<V> valueClass,
            Object... objects) {
        final LinkedMap<K,V> map = new LinkedMap<>();
        for (int i=0; i<objects.length; i+=2) {
            map.put((K) objects[i], (V) objects[i+1]);
        }
        return map;
    }


    /**
     * CAUTION: this method block static checking!
     * @param <K>
     * @param <V>
     * @param objects
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <K,V> LinkedMap<K,V> create(Object... objects) {
        final LinkedMap<K,V> map = new LinkedMap<>();
        for (int i=0; i<objects.length; i+=2) {
            map.put((K) objects[i], (V) objects[i+1]);
        }
        return map;
    }

    private LinkedEntry<K,V> head;

    public LinkedMap() {}

    /** Copy constructor. */
    public LinkedMap(Map<? extends K, ? extends V> copy) {
        for (Entry<? extends K, ? extends V> e : copy.entrySet()) {
            put(e.getKey(), e.getValue());
        }
    }

    /** Copy constructor. */
    public LinkedMap(Iterable<Entry<? extends K, ? extends V>> copy) {
        for (Entry<? extends K, ? extends V> e : copy) {
            put(e.getKey(), e.getValue());
        }
    }

    public <W> LinkedMap<K,W> transform(Function<V,W> converter) {
        LinkedMap<K,W> map = new LinkedMap<>();
        for (Map.Entry<K,V> t : this) {
            map.put(t.getKey(), converter.apply(t.getValue()));
        }
        return map;
    }

    public LinkedMap<K,V> getUnmodifiableCopy() {
        return UnmodifiableLinkedMap.copy(this);
    }

    public List<Map.Entry<K,V>> toEntryList() {
        List<Map.Entry<K,V>> list = new ArrayList<>(size());
        for (Map.Entry<K,V> e : this) {
            list.add(e);
        }
        return list;
    }

    @Override
    public void clear() {
        head = null;
    }

    @Override
    public boolean isEmpty() {
        return head == null;
    }

    @Override
    public int size() {
        int size = 0;
        LinkedEntry<K,V> current = this.head;
        while (current != null) {
            size++;
            current = current.getNext();
        }
        return size;
    }

    public LinkedEntry<K,V> getEntryAtIndex(int index) {
        LinkedEntry<K,V> current = head;
        int i = index;
        while (i > 0 && current.getNext() != null) {
            current = current.getNext();
            i--;
        }
        return current;
    }

    /**
     * Adds a new entry. For performance reasons if an entry is already
     * present with the same key the given value will be inserted into
     * the existing entry.
     * @param entry
     * @return
     */
    public V addEntry(LinkedEntry<K,V> entry) {
        LinkedEntry<K,V> node = getEntryWithKey(entry.getKey());
        V oldValue = null;
        if (node != null) {
            oldValue = node.getValue();
            node.setValue(entry.getValue());
        } else {
            linkEntry(entry);
        }
        return oldValue;
    }

    @Override
    public V put(K key, V value) {
        LinkedEntry<K,V> node = getEntryWithKey(key);
        V oldValue = null;
        if (node != null) {
            oldValue = node.getValue();
            node.setValue(value);
        } else {
            linkEntry(createEntry(key, value));
        }
        return oldValue;
    }

    protected LinkedEntry<K,V> createEntry(K key, V value) {
        return new LEntry<>(key, value);
    }

    protected void linkEntry(LinkedEntry<K,V> entry) {
        addAtEnd(entry);
    }

    /** Preserves insertion order. */
    protected void addAtEnd(LinkedEntry<K, V> entry) {
        LinkedEntry<K,V> last = head;
        if (last == null) {
            head = entry;
        } else {
            while (last.getNext() != null) {
                last = last.getNext();
            }
            last.setNext(entry);
        }
        entry.setNext(null); // to be sure!
    }

    /** Reverses insertion order but faster. */
    protected void addAtBeginning(LinkedEntry<K,V> entry) {
        entry.setNext(head);
        head = entry;
    }

    public V getOrCreate(K key, Supplier<V> supplier) {
        V v = get(key);
        if (v == null) {
            v = supplier.get();
            put(key, v);
        }
        return v;
    }

    @Override
    public V get(Object key) {
        @SuppressWarnings("unchecked")
        LinkedEntry<K,V> result = getEntryWithKey((K)key);
        if (result != null) {
            return result.getValue();
        }
        return null;
    }

    public LinkedEntry<K,V> getEntryWithKey(K key) {
        LinkedEntry<K,V> current = head;
        while(current != null) {
            K k = current.getKey();
            if (k == key || k.equals(key)) {
                return current;
            }
            current = current.getNext();
        }
        return null;
    }

    @Override
    public V remove(Object key) {
        @SuppressWarnings("unchecked")
        LinkedEntry<K,V> removed = removeChild((K)key);
        if (removed != null) {
            return removed.getValue();
        }
        return null;
    }

    private LinkedEntry<K,V> removeChild(K key) {
        LinkedEntry<K,V> current = head;
        LinkedEntry<K,V> prev = head;
        while(current != null) {
            K k = current.getKey();
            if (k == key || k.equals(key)) {
                if (current == head) {
                    head = current.getNext();
                } else {
                    prev.setNext(current.getNext());
                }
                current.setNext(null);
                return current;
            }
            prev = current;
            current = current.getNext();
        }
        return null;
    }

    private static final LinkedEntry<?,?> START =
            new LEntry<Object,Object>(null, null);

    @Override
    public Iterator<Entry<K,V>> iterator() {
        return new Iterator<Entry<K,V>>() {
            @SuppressWarnings("unchecked")
            private LinkedEntry<K,V> current = (LinkedMap.this.head == null) ?
                    null :
                    (LinkedEntry<K,V>)START;
            private LinkedEntry<K,V> prev = null;

            @Override
            public boolean hasNext() {
                return START != null && (
                        current == START ||
                        (current != null && current.getNext() != null));
            }

            @Override
            public Entry<K,V> next() {
                if (current == START) {
                    current = LinkedMap.this.head;
                    return current;
                }
                prev = current;
                current = current.getNext();
                return current;
            }

            @Override
            @SuppressWarnings("unchecked")
            public void remove() {
                if (current != null && current != START) {
                    if (current == prev) {
                        throw new IllegalStateException(
                                "cannot call remove() twice");
                    }
                    if (prev != null) {
                        prev.setNext(current.getNext());
                        current.setNext(null);
                        current = prev;
                    } else {
                        LinkedMap.this.head = current.getNext();
                        current.setNext(null);
                        current = (LinkedEntry<K, V>) START;
                    }
                } else {
                    throw new IllegalStateException(
                                "next() was not called first");
                }
            }

        };
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean containsKey(Object key) {
        return getEntryWithKey((K)key) != null;
    }

    @Override
    public boolean containsValue(Object value) {
        for (Entry<K,V> t : this) {
            final V v = t.getValue();
            if ((value == null && v == null) ||
                    (value != null && value.equals(v)) ) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        for (Entry<? extends K,? extends V> entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    public List<K> keyList() {
        return new AbstractList<K>() {
            @Override
            public Iterator<K> iterator() {
                return new Iterator<K>() {
                    Iterator<Entry<K,V>> it = LinkedMap.this.iterator();

                    @Override
                    public boolean hasNext() {
                        return it.hasNext();
                    }

                    @Override
                    public K next() {
                        return it.next().getKey();
                    }

                    @Override
                    public void remove() {
                        it.remove();
                    }
                };
            }

            @Override
            public int size() {
                return LinkedMap.this.size();
            }

            @Override
            public K get(int index) {
                return LinkedMap.this.getEntryAtIndex(index).getKey();
            }

        };
    }

    @Override
    public Set<K> keySet() {
        return new AbstractSet<K>() {
            @Override
            public Iterator<K> iterator() {
                return new Iterator<K>() {
                    Iterator<Entry<K,V>> it = LinkedMap.this.iterator();

                    @Override
                    public boolean hasNext() {
                        return it.hasNext();
                    }

                    @Override
                    public K next() {
                        return it.next().getKey();
                    }

                    @Override
                    public void remove() {
                        it.remove();
                    }
                };
            }

            @Override
            public int size() {
                return LinkedMap.this.size();
            }

        };
    }

    @Override
    public List<V> values() {
        return new AbstractList<V>() {
            @Override
            public Iterator<V> iterator() {
                return new Iterator<V>() {
                    private Iterator<Entry<K,V>> it = LinkedMap.this.iterator();

                    @Override
                    public boolean hasNext() {
                        return it.hasNext();
                    }

                    @Override
                    public V next() {
                        return it.next().getValue();
                    }

                    @Override
                    public void remove() {
                        it.remove();
                    }
                };
            }

            @Override
            public int size() {
                return LinkedMap.this.size();
            }

            @Override
            public V get(int index) {
                return LinkedMap.this.getEntryAtIndex(index).getValue();
            }
        };
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return new AbstractSet<Entry<K, V>>() {

            @Override
            public Iterator<Entry<K, V>> iterator() {
                return LinkedMap.this.iterator();
            }

            @Override
            public int size() {
                return LinkedMap.this.size();
            }
        };
    }

    @Override
    public int hashCode() {
        int hash = 7;
        for (Entry<K,V> e : this) {
            hash = 17 * hash + Objects.hashCode(e.getKey());
            hash = 17 * hash + Objects.hashCode(e.getValue());
        }
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
        final Map<?, ?> other = (Map<?, ?>) obj;
        if (other.size() != size()) {
            return false;
        }
        for (Entry<?,?> e : this) {
            Object ov = other.get(e.getKey());
            Object tv = e.getValue();
            if (!Objects.equals(ov, tv)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(getClass().getSimpleName()).append("{");
        for (Entry<K,V> e : this) {
            if (buf.length() < 3) {
                buf.append(",");
            }
            buf.append("Entry{key=").append(e.getKey());
            buf.append(", value=").append(e.getValue());
            buf.append("}");
        }
        buf.append("}");
        return buf.toString();
    }
}
