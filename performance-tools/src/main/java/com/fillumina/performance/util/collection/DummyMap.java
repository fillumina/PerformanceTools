package com.fillumina.performance.util.collection;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;

/**
 * A Map which does not do anything useful. It's just a placeholder.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class DummyMap<K,V> extends AbstractMap<K,V> {
    public static final DummyMap<?,?> INSTANCE = new DummyMap<>();

    @SuppressWarnings("unchecked")
    public static <K,V> DummyMap<K,V> instance() {
        return (DummyMap<K, V>) INSTANCE;
    }

    private final Iterator<Entry<K, V>> iterator = new Iterator<Entry<K, V>>() {
        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public Entry<K, V> next() {
            return null;
        }

        @Override
        public void remove() {
            // do nothing
        }
    };

    private final Set<Entry<K,V>> set = new AbstractSet<Entry<K,V>>() {

        @Override
        public int size() {
            return 0;
        }

        @Override
        public Iterator<Entry<K,V>> iterator() {
            return iterator;
        }

    };

    @Override
    public Set<Entry<K, V>> entrySet() {
        return set;
    }

    @Override
    public V put(K key, V value) {
        return value;
    }
}
