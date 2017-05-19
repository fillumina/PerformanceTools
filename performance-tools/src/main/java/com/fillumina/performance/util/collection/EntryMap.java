package com.fillumina.performance.util.collection;

import java.util.Map;
import java.util.Objects;

/**
 * Implementation of {@link Map.Entry}.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class EntryMap<K,V> implements Map.Entry<K,V> {
    private final K key;
    private V value;

    public EntryMap(K key) {
        this.key = key;
    }

    public EntryMap(K key, V value) {
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
        V oldValue = value;
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
        if (!(obj instanceof Map.Entry)) {
            return false;
        }
        final Map.Entry<?, ?> other = (Map.Entry<?, ?>) obj;
        return Objects.equals(this.key, other.getKey()) &&
            Objects.equals(this.value, other.getValue());
    }

    @Override
    public String toString() {
        return "EntryMap{" + "key=" + key + ", value=" + value + '}';
    }
}
