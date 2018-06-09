package com.fillumina.performance.util.tname;

import com.fillumina.performance.util.collection.IndexedArrayMap;
import java.util.Objects;

/**
 * Because {@link TName} implements {@link CharSequence} this map has
 * uses equality over string representations to allow searching by
 * {@link String} or {@link CharSequence}.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class TNameMap<T extends TNamed>
        extends IndexedArrayMap<TName, T> {
    private static final long serialVersionUID = 1L;

    public TNameMap() {
        super();
    }

    public TNameMap(int size) {
        super(size);
    }

    public TNameMap(TNameMap<T> copy) {
        super(copy);
    }

    protected TNameMap(TNameMap<T> delegate, boolean notUsed) {
        super(delegate, true);
    }

    protected static class UnmodifiableView<T extends TNamed> extends TNameMap<T> {
        private static final long serialVersionUID = 1L;

        UnmodifiableView(TNameMap<T> delegate) {
            super(delegate, true);
        }

        @Override
        public boolean isUnmodifiable() {
            return true;
        }

        @Override
        public UnmodifiableView<T> clone() {
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
        public T setValueAtIndex(int index, T value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Override
        public T remove(Object key) {
            throw new UnsupportedOperationException();
        }

        @Override
        public T put(TName key, T value) {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    protected UnmodifiableView<T> createUnmodifiable() {
        return new UnmodifiableView<>(this);
    }

    @Override
    public UnmodifiableView<T> unmodifiable() {
        return (UnmodifiableView<T>) super.unmodifiable();
    }

    @Override
    public boolean equals(Object a, Object b) {
        return a == b || Objects.toString(a).equals(Objects.toString(b));
    }

    public TNameMap<T> add(T t) {
        put(t.getName(), t);
        return this;
    }

    public T put(T t) {
        return put(t.getName(), t);
    }
}
