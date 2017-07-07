package com.fillumina.performance.util.collection;

import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ReMapper<K,V,W> implements Map<K,V>, Serializable {
    private static final long serialVersionUID = 1L;

    private static final Function<?,?> NULL = (t) -> { return null; };

    @SuppressWarnings("unchecked")
    public static <X,Y> Function<X,Y> nullFunction() {
        return (Function<X, Y>) NULL;
    }

    private final Map<K,W> delegate;
    private final Function<W,V> function;
    private final Function<V,W> reverse;

    public ReMapper(Map<K,W> delegate,
            Function<W, V> function,
            Function<V, W> reverse) {
        this.delegate = delegate;
        this.function = function;
        this.reverse = reverse;
    }

    /** Read only. */
    public ReMapper(Map<K,W> delegate, Function<W, V> function) {
        this.delegate = delegate;
        this.function = function;
        this.reverse = nullFunction();
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return delegate.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return delegate.containsValue(value);
    }

    @Override
    public V get(Object key) {
        return function.apply(delegate.get(key));
    }

    @Override
    public V put(K key, V value) {
        return function.apply(delegate.put(key, reverse.apply(value)));
    }

    @Override
    public V remove(Object key) {
        return function.apply(delegate.remove(key));
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        for (Map.Entry<? extends K, ? extends V> e : m.entrySet()) {
            put(e.getKey(), e.getValue());
        }
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    private class EntryIterator implements Iterator<Entry<K,V>> {
        private Iterator<Entry<K,W>> it = delegate.entrySet().iterator();

        @Override
        public boolean hasNext() {
            return it.hasNext();
        }

        @Override
        public Entry<K, V> next() {
            Entry<K,W> e = it.next();
            return new EntryMap<>(e.getKey(), function.apply(e.getValue()));
        }

        @Override
        public void remove() {
            it.remove();
        }
    }

    private class EntrySet extends AbstractSet<Entry<K,V>> {
        @Override
        public Iterator<Entry<K, V>> iterator() {
            return new EntryIterator();
        }

        @Override
        public int size() {
            return ReMapper.this.size();
        }

    }

    private EntrySet entrySet;
    @Override
    public Set<Entry<K, V>> entrySet() {
        if (entrySet == null) {
            entrySet = new EntrySet();
        }
        return entrySet;
    }

    private class ValueIterator implements Iterator<V> {
        private Iterator<W> it = delegate.values().iterator();

        @Override
        public boolean hasNext() {
            return it.hasNext();
        }

        @Override
        public V next() {
            W w = it.next();
            return function.apply(w);
        }

        @Override
        public void remove() {
            it.remove();
        }
    }

    private class ValueCollection extends AbstractCollection<V> {
        @Override
        public Iterator<V> iterator() {
            return new ValueIterator();
        }

        @Override
        public int size() {
            return ReMapper.this.size();
        }
    }

    private ValueCollection valueCollection;
    @Override
    public Collection<V> values() {
        if (valueCollection == null) {
            valueCollection = new ValueCollection();
        }
        return valueCollection;
    }

    @Override
    public Set<K> keySet() {
        return delegate.keySet();
    }
}
