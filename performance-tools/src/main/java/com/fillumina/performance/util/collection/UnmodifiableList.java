package com.fillumina.performance.util.collection;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;

/**
 *  Immutable Generic List
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class UnmodifiableList<T> extends AbstractList<T>
        implements Serializable, Cloneable {
    private static final long serialVersionUID = 1L;

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

    @Override
    public Object clone() throws CloneNotSupportedException {
        return this; // it's immutable
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(this.array);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final UnmodifiableList<?> other = (UnmodifiableList<?>) obj;
        return Arrays.deepEquals(this.array, other.array);
    }

    @Override
    public String toString() {
        return "UnmodifiableList" + Arrays.toString(array);
    }
}
