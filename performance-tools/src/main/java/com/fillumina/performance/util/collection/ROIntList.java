package com.fillumina.performance.util.collection;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.Arrays;

/**
 * Read only list of int.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ROIntList extends AbstractList<Integer> implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final int[] EMPTY_ARRAY = new int[0];

    public static final ROIntList EMPTY = new ROIntList(EMPTY_ARRAY);

    private final int[] array;

    public ROIntList(int... array) {
        this.array = array;
    }

    public int getInt(int index) {
        return array[index];
    }

    public int[] toIntArray() {
        if (array == null || array.length == 0) {
            return EMPTY_ARRAY;
        }
        return Arrays.copyOf(array, array.length);
    }

    @Override
    public Integer get(int index) {
        return array[index];
    }

    @Override
    public int size() {
        return array == null ? 0 : array.length;
    }
}
