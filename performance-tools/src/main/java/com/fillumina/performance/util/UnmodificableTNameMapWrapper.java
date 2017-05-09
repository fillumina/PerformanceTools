package com.fillumina.performance.util;

import com.fillumina.performance.infrastructure.TN;
import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class UnmodificableTNameMapWrapper<T>
        implements Map<TName, T>, Serializable {
    private static final long serialVersionUID = 1L;

    private final Map<TName,T> delegate;

    public UnmodificableTNameMapWrapper(Map<TName,T> m) {
        this.delegate = m;
    }

    public T get(String name) {
        return get(TN.name(name));
    }

    public Set<String> nameSet() {
        return new AbstractSet<String>() {
            @Override
            public Iterator<String> iterator() {
                return new Iterator<String>() {
                    private final Iterator<TName> it =
                        UnmodificableTNameMapWrapper.this.keySet().iterator();

                    @Override
                    public boolean hasNext() {
                        return it.hasNext();
                    }

                    @Override
                    public String next() {
                        return it.next().toString();
                    }
                };
            }

            @Override
            public int size() {
                return UnmodificableTNameMapWrapper.this.size();
            }

        };
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
    public T get(Object key) {
        return delegate.get(key);
    }

    @Override
    public T put(TName key, T value) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public T remove(Object key) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public void putAll(Map<? extends TName, ? extends T> m) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public Set<TName> keySet() {
        return Collections.unmodifiableSet(delegate.keySet());
    }

    @Override
    public Collection<T> values() {
        return Collections.unmodifiableCollection(delegate.values());
    }

    @Override
    public Set<Entry<TName, T>> entrySet() {
        return Collections.unmodifiableSet(delegate.entrySet());
    }

    @Override
    public boolean equals(Object o) {
        return delegate.equals(o);
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }
}
