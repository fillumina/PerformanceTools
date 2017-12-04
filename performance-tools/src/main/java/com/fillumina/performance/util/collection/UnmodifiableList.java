package com.fillumina.performance.util.collection;

import java.util.AbstractList;
import java.util.Collection;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class UnmodifiableList<T> extends AbstractList<T> {

    private final T[] array;

    /** Reads all the items from the give collection. */
    @SuppressWarnings("unchecked")
    public UnmodifiableList(Collection<T> coll) {
        this((T[])coll.toArray());
    }

    public UnmodifiableList(T... array) {
        this.array = array;
    }

    @Override
    public T get(int index) {
        return array[index];
    }

    @Override
    public int size() {
        return array.length;
    }
}
