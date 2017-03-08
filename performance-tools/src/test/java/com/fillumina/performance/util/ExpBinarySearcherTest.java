package com.fillumina.performance.util;

import com.fillumina.performance.util.ExpBinarySearcher.Condition;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ExpBinarySearcherTest {

    @Test
    public void shouldFind0() {
        assertFound(0);
    }

    @Test
    public void shouldFind1() {
        assertFound(1);
    }

    @Test
    public void shouldFind2() {
        assertFound(2);
    }

    @Test
    public void shouldFind3() {
        assertFound(3);
    }

    @Test
    public void shouldFind4() {
        assertFound(4);
    }

    @Test
    public void shouldFind5() {
        assertFound(5);
    }

    private void assertFound(int value) {
        assertFoundLessOrEquals(value);
        assertFoundGreaterOrEqualsShort(value);

        assertFoundLess(value);
        assertFoundGreaterShort(value);

        assertFoundGreaterOrEquals(value);
        assertFoundLessOrEqualsShort(value);

        assertFoundGreater(value);
        assertFoundLessShort(value);
    }

    private void assertFoundGreaterOrEquals(final int value) {
        int result = ExpBinarySearcher.excludingSearch(0, 128, new Condition() {
            @Override
            public boolean isSatisfied(int v) {
                return !(v >= value);  // <------------------<<<
            }
        });
        assertEquals(value, result);
    }

    private void assertFoundGreater(final int value) {
        int result = ExpBinarySearcher.includingSearch(0, 128, new Condition() {
            @Override
            public boolean isSatisfied(int v) {
                return !(v > value);  // <------------------<<<
            }
        });
        assertEquals(value, result);
    }

    private void assertFoundLessOrEquals(final int value) {
        int result = ExpBinarySearcher.includingSearch(0, 128, new Condition() {
            @Override
            public boolean isSatisfied(int v) {
                return v <= value;  // <------------------<<<
            }
        });
        assertEquals(value, result);
    }

    private void assertFoundLess(final int value) {
        int result = ExpBinarySearcher.excludingSearch(0, 128, new Condition() {
            @Override
            public boolean isSatisfied(int v) {
                return v < value;  // <------------------<<<
            }
        });
        assertEquals(value, result);
    }

    private void assertFoundLessOrEqualsShort(final int value) {
        int result = ExpBinarySearcher.searchLessOrEquals(0, 128, new Condition() {
            @Override
            public boolean isSatisfied(int v) {
                return v <= value;  // <------------------<<<
            }
        });
        assertEquals(value, result);
    }

    private void assertFoundLessShort(final int value) {
        int result = ExpBinarySearcher.searchLess(0, 128, new Condition() {
            @Override
            public boolean isSatisfied(int v) {
                return v < value;  // <------------------<<<
            }
        });
        assertEquals(value, result);
    }

    private void assertFoundGreaterOrEqualsShort(final int value) {
        int result = ExpBinarySearcher.searchGreaterOrEquals(0, 128, new Condition() {
            @Override
            public boolean isSatisfied(int v) {
                return v >= value;  // <------------------<<<
            }
        });
        assertEquals(value, result);
    }

    private void assertFoundGreaterShort(final int value) {
        int result = ExpBinarySearcher.searchGreater(0, 128, new Condition() {
            @Override
            public boolean isSatisfied(int v) {
                return v > value;  // <------------------<<<
            }
        });
        assertEquals(value, result);
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
