package com.fillumina.performance.util.collection;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.Arrays;

/**
 * Read only list of int.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class UnmodifiableIntList extends AbstractList<Integer>
        implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final int[] EMPTY_ARRAY = new int[0];

    public static final UnmodifiableIntList EMPTY =
            new UnmodifiableIntList(EMPTY_ARRAY);

    private final int[] array;

    public UnmodifiableIntList(int... array) {
        this.array = array;
    }

    public int getInt(int index) {
        return array[index];
    }

    public int[] toIntArray() {
        if (array == null || array.length == 0) {
            return EMPTY_ARRAY;
        }
        return array.clone();
    }

    @Override
    public Integer get(int index) {
        return array[index];
    }

    @Override
    public int size() {
        return array == null ? 0 : array.length;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return this; // it's immutable
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.array);
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
        final UnmodifiableIntList other = (UnmodifiableIntList) obj;
        return Arrays.equals(this.array, other.array);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + Arrays.toString(array);
    }
}
