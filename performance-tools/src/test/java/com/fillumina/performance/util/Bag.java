package com.fillumina.performance.util;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * A simple collection that counts the number of times an object appears in it.
 *
 * @author Francesco Illuminati
 */
public class Bag<T> implements Set<T> {
    private final Map<T, Long> map = new HashMap<>();
    private final Set<T> set = map.keySet();
    private final Map<T, Long> umap = Collections.unmodifiableMap(map);

    public Map<T, Long> getMap() {
        return umap;
    }

    public long getCount(final T key) {
        final Long value = map.get(key);
        return value == null ? 0 : value;
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean add(final T key) {
        Long counter = map.get(key);
        if (counter == null) {
            map.put(key, 1L);
            return false;
        }
        map.put(key, counter + 1);
        return true;
    }

    @Override
    public String toString() {
        return map.toString();
    }

    @Override
    public boolean isEmpty() {
        return set.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return set.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return set.iterator();
    }

    @Override
    public Object[] toArray() {
        return set.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return set.toArray(a);
    }

    @Override
    public boolean remove(Object o) {
        return set.remove(o);
    }

    @Override
    public boolean containsAll(
            Collection<?> c) {
        return set.containsAll(c);
    }

    @Override
    public boolean addAll(
            Collection<? extends T> c) {
        return set.addAll(c);
    }

    @Override
    public boolean retainAll(
            Collection<?> c) {
        return set.retainAll(c);
    }

    @Override
    public boolean removeAll(
            Collection<?> c) {
        return set.removeAll(c);
    }

    @Override
    public void clear() {
        set.clear();
    }

    @Override
    public boolean equals(Object o) {
        return set.equals(o);
    }

    @Override
    public int hashCode() {
        return set.hashCode();
    }
}
