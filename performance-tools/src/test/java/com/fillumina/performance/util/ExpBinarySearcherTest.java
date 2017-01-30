package com.fillumina.performance.util;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ExpBinarySearcherTest {

    public static void main(final String[] args) {
        for (int i=0; i<10; i++) {
            System.out.println("i= " + (1 << i));
        }
    }

    @Test
    public void shouldFoundTheValue() {
        final int max = 32;
        for (int i=0; i<max; i++) {
            assertValueFullComparable(i, max);
        }
    }

    private void assertValueFullComparable(final int value, final int max) {
        int result = ExpBinarySearcher.search(max, new Comparable<Integer>() {
            @Override
            public int compareTo(Integer o) {
                return o.compareTo(value);
            }
        });

        assertEquals(value, result);
    }

    @Test
    public void shouldNotFindTheValueIfNotPresent() {
        int result = ExpBinarySearcher.search(64, new Comparable<Integer>() {
            @Override
            public int compareTo(Integer o) {
                return o.compareTo(65); // not in range
            }
        });

        assertEquals(-1, result);
    }

    @Test
    public void shouldFindTheValueEvenIfNotEqualSingle() {
        assertValueCondition(0, 16, 32);
    }

    @Test
    public void shouldFindTheValueEvenIfNotEqual() {
        final int max = 32;
        for (int i=0; i<max; i++) {
            assertValueCondition(0, i, max);
        }
    }

    @Test
    public void shouldFindTheValueEvenIfNotEqualMaxNotPowerOf2() {
        final int max = 19;
        for (int i=0; i<max; i++) {
            assertValueCondition(0, i, max);
        }
    }

    @Test
    public void shouldFindTheValueEvenIfNotEqualStartNotPowerOf2() {
        final int min = 3;
        final int max = 19;
        for (int i=min; i<max; i++) {
            assertValueCondition(min, i, max);
        }
    }

    private void assertValueCondition(int start,
            final int value, final int max) {
        int result = ExpBinarySearcher.search(start, max,
                new Comparable<Integer>() {
            @Override
            public int compareTo(Integer o) {
                if (o <= value) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });

        assertEquals(value, result);
    }


}
