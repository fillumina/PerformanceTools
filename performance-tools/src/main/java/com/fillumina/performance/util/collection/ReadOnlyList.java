package com.fillumina.performance.util.collection;

import java.util.AbstractList;
import java.util.Collection;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ReadOnlyList<T> extends AbstractList<T> {

    private final T[] array;

    @SuppressWarnings("unchecked")
    public ReadOnlyList(Collection<T> coll) {
        this((T[])coll.toArray());
    }

    public ReadOnlyList(T... array) {
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
