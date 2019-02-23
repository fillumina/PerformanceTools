package com.fillumina.performance.util.pathname;

import com.fillumina.performance.util.collection.IndexedHashMap;
import java.util.Objects;

/**
 * Because {@link PathName} implements {@link CharSequence} this map
 * uses equality over strings to allow searching by whatever
 * Object key has a matching string representation.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class PathNamedMap<T extends PathNamed>
        extends IndexedHashMap<PathName, T> {
    private static final long serialVersionUID = 1L;

    public PathNamedMap() {
        super();
    }

    public PathNamedMap(int size) {
        super(size);
    }

    public PathNamedMap(PathNamedMap<T> copy) {
        super(copy);
    }

    protected PathNamedMap(PathNamedMap<T> delegate, boolean notUsed) {
        super(delegate, true);
    }

    @Override
    protected PathNamedMap<T> createUnmodifiable() {
        return new PathNamedMap<>(this, true);
    }

    @Override
    public PathNamedMap<T> unmodifiableView() {
        return (PathNamedMap<T>) super.unmodifiableView();
    }

    /**
     * This is the key method: by this it is possible to search into the map by
     * using:<br>{@code
     * map.get("one : two")
     * }<br>along with using a {@link PathName} such as with:<br>{@code
     * map.get(PathName.getRoot().append("one").append("two"))
     * }.
     */
    @Override
    public boolean equalsKey(Object a, Object b) {
        return a == b || Objects.toString(a).equals(Objects.toString(b));
    }

    public PathNamedMap<T> add(T t) {
        put(t.getPathName(), t);
        return this;
    }

    public T put(T t) {
        return put(t.getPathName(), t);
    }
}
