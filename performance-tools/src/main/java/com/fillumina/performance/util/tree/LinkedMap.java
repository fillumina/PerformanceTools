package com.fillumina.performance.util.tree;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

/**
 * A {@link Map} with very low memory requirements.
 * It is based on a single linked list of entries so it uses very little memory
 * but it is slow compared to the classic hash solution.
 * <p>
 * This class is not thread safe.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class LinkedMap<K,V>
        implements Iterable<Entry<K,V>>, Map<K,V>, Serializable {
    private static final long serialVersionUID = 1L;

    private static class LEntry<K,V> implements Entry<K,V> {
        private final K key;
        private V value;
        private LEntry<K,V> next;

        public LEntry(K key, V value) {
            this.key = key;
            this.value = value;
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
            final LEntry<?, ?> other = (LEntry<?, ?>) obj;
            if (!Objects.equals(this.key, other.key)) {
                return false;
            }
            if (!Objects.equals(this.value, other.value)) {
                return false;
            }
            return true;
        }
    }

    /** Faster insertions but iterates with reverse order of insertion. */
    public static class Inverse<K,V> extends LinkedMap<K,V> {
        private static final long serialVersionUID = 1L;

        public Inverse() {
        }

        public Inverse(Map<K, V> copy) {
            super(copy);
        }

        public Inverse(Collection<Entry<K, V>> copy) {
            super(copy);
        }

        @Override
        protected void addEntry(LEntry<K, V> entry) {
            addAtBeginning(entry);
        }

    }

    @SuppressWarnings("unchecked")
    public static <K,V> Map<K, V> create(Object... objects) {
        final Map<K,V> map = new LinkedMap<>();
        for (int i=0; i<objects.length; i+=2) {
            map.put((K)objects[i], (V) objects[i+1]);
        }
        return map;
    }

    private LEntry<K,V> head;

    public LinkedMap() {}

    /** Copy constructor. */
    public LinkedMap(Map<K,V> copy) {
        this(copy.entrySet());
    }

    /** Copy constructor. */
    public LinkedMap(Collection<Entry<K,V>> copy) {
        for (Entry<K,V> e : copy) {
            put(e.getKey(), e.getValue());
        }
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
        LEntry<K,V> current = this.head;
        while (current != null) {
            size++;
            current = current.next;
        }
        return size;
    }

    @Override
    public V put(K key, V value) {
        LEntry<K,V> node = getChild(key);
        V oldValue = null;
        if (node != null) {
            oldValue = node.getValue();
            node.setValue(value);
        } else {
            addEntry(new LEntry<>(key, value));
        }
        return oldValue;
    }

    protected void addEntry(LEntry<K,V> entry) {
        addAtEnd(entry);
    }

    /** Preserves insertion order. */
    protected void addAtEnd(LEntry<K, V> entry) {
        LEntry<K,V> last = head;
        if (last == null) {
            head = entry;
        } else {
            while (last.next != null) {
                last = last.next;
            }
            last.next = entry;
        }
        entry.next = null; // to be sure!
    }

    /** Reverses insertion order but faster. */
    protected void addAtBeginning(LEntry<K,V> entry) {
        entry.next = head;
        head = entry;
    }

    @Override
    public V get(Object key) {
        @SuppressWarnings("unchecked")
        LEntry<K,V> result = getChild((K)key);
        if (result != null) {
            return result.getValue();
        }
        return null;
    }

    private LEntry<K,V> getChild(K key) {
        LEntry<K,V> current = head;
        while(current != null) {
            if (current.key == key || current.key.equals(key)) {
                return current;
            }
            current = current.next;
        }
        return null;
    }

    @Override
    public V remove(Object key) {
        @SuppressWarnings("unchecked")
        LEntry<K,V> removed = removeChild((K)key);
        if (removed != null) {
            return removed.getValue();
        }
        return null;
    }

    private LEntry<K,V> removeChild(K key) {
        LEntry<K,V> current = head;
        LEntry<K,V> prev = head;
        while(current != null) {
            if (current.key == key || current.key.equals(key)) {
                if (current == head) {
                    head = current.next;
                } else {
                    prev.next = current.next;
                }
                current.next = null;
                return current;
            }
            prev = current;
            current = current.next;
        }
        return null;
    }

    private static final LEntry<?,?> START = new LEntry<Object,Object>(null, null);

    @Override
    public Iterator<Entry<K,V>> iterator() {
        return new Iterator<Entry<K,V>>() {
            @SuppressWarnings("unchecked")
            private LEntry<K,V> current = (LinkedMap.this.head == null) ?
                    null :
                    (LEntry<K,V>)START;
            private LEntry<K,V> prev = null;

            @Override
            public boolean hasNext() {
                return current == START ||
                        (current != null && current.next != null);
            }

            @Override
            public Entry<K,V> next() {
                if (current == START) {
                    current = LinkedMap.this.head;
                    return current;
                }
                prev = current;
                current = current.next;
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
                        prev.next = current.next;
                        current.next = null;
                        current = prev;
                    } else {
                        LinkedMap.this.head = current.next;
                        current.next = null;
                        current = (LEntry<K, V>) START;
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
        return getChild((K)key) != null;
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
    public Collection<V> values() {
        return new AbstractSet<V>() {
            @Override
            public Iterator<V> iterator() {
                return new Iterator<V>() {
                    Iterator<Entry<K,V>> it = LinkedMap.this.iterator();

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


}
