package com.fillumina.performance.util.tname;

import com.fillumina.performance.util.collection.IndexedHashMap;
import java.util.Objects;

/**
 * Because {@link TName} implements {@link CharSequence} this map
 * uses equality over identity to allow searching by whatever can be
 * matched with that.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class TNameMap<T extends TNamed>
        extends IndexedHashMap<TName, T> {
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

    @Override
    protected TNameMap<T> createUnmodifiable() {
        return new TNameMap<>(this, true);
    }

    @Override
    public TNameMap<T> unmodifiableView() {
        return (TNameMap<T>) super.unmodifiableView();
    }

    /*
     * This is the key method: by this it is possible to search into the map by
     * using <p>
     * {@code map.get("one : two")}
     * <p> along with using a {@link TName} such as with <p>
     * {@code map.get(TName.getRoot().append("one").append("two"))}.
     */
    @Override
    public boolean equalsKey(Object a, Object b) {
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
