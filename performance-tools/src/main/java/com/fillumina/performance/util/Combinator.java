package com.fillumina.performance.util;

import com.fillumina.performance.util.Combinator.IntArrayCursorList;
import java.util.AbstractList;
import java.util.Iterator;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class Combinator implements Iterable<IntArrayCursorList> {

    private int combinations;
    private int[] maxValues;

    public Combinator(int... maxValues) {
        this.combinations = calculateCombinations(maxValues);
        this.maxValues = maxValues;
    }

    private static int calculateCombinations(int[] maxValues) {
        int c = 1;
        for (int m : maxValues) {
            c *= m;
        }
        return c;
    }

    /** Unmodifiable view to the array of solutions. */
    public static class IntArrayCursorList extends AbstractList<Integer> {
        private final int[] array;

        public IntArrayCursorList(int[] array) {
            this.array = array;
        }

        @Override
        public Integer get(int index) {
            return array[index];
        }

        @Override
        public int size() {
            return array.length;
        }

        public int getInt(int index) {
            return array[index];
        }

        public int[] getArrayCopy() {
            int[] copy = new int[array.length];
            System.arraycopy(array, 0, copy, 0, array.length);
            return copy;
        }
    }

    /** Note that the returned list is a cursor that changes with new values. */
    @Override
    public Iterator<IntArrayCursorList> iterator() {
        return new Iterator<IntArrayCursorList>() {
            private int counter;
            private int position;
            private final int[] values = new int[maxValues.length];
            private final IntArrayCursorList unmodifiableCursorList =
                    new IntArrayCursorList(values);

            @Override
            public boolean hasNext() {
                return counter < combinations;
            }

            @Override
            public IntArrayCursorList next() {
                if (counter != 0) {
                    increment(position);
                }
                counter++;
                return unmodifiableCursorList;
            }

            private void increment(int pos) {
                values[pos]++;
                if (values[pos] >= maxValues[pos]) {
                    values[pos] = 0;
                    increment(pos + 1);
                }
            }
        };
    }

}
