package com.fillumina.performance.util.collection;

import java.util.AbstractList;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ReadOnlyList<T> extends AbstractList<T> {

    private final T[] array;

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
