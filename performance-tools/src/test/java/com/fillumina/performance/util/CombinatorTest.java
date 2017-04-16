package com.fillumina.performance.util;

import com.fillumina.performance.util.Combinator.IntArrayCursorList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class CombinatorTest {

    public static void main(final String[] args) {
        for (List<Integer> v : new Combinator(2, 3, 5)) {
            System.out.println(v);
        }
    }

    @Test
    public void shoultGenerateIterator() {
        Iterator<IntArrayCursorList> it = new Combinator(2).iterator();
        assertTrue(it.hasNext());
        assertEquals(0, it.next().get(0), 0);

        assertTrue(it.hasNext());
        assertEquals(1, it.next().get(0), 0);

        assertFalse(it.hasNext());
    }

    @Test
    public void shouldMatchMultipleCombinations() {
        int[][] solution = new int[][] {
            {0, 0, 0},
            {1, 0, 0},
            {0, 1, 0},
            {1, 1, 0},
            {0, 2, 0},
            {1, 2, 0},
            {0, 0, 1},
            {1, 0, 1},
            {0, 1, 1},
            {1, 1, 1},
            {0, 2, 1},
            {1, 2, 1},
            {0, 0, 2},
            {1, 0, 2},
            {0, 1, 2},
            {1, 1, 2},
            {0, 2, 2},
            {1, 2, 2},
            {0, 0, 3},
            {1, 0, 3},
            {0, 1, 3},
            {1, 1, 3},
            {0, 2, 3},
            {1, 2, 3},
            {0, 0, 4},
            {1, 0, 4},
            {0, 1, 4},
            {1, 1, 4},
            {0, 2, 4},
            {1, 2, 4},
        };

        Iterator<IntArrayCursorList> it = new Combinator(2, 3, 5).iterator();
        for (int i=0; i<solution.length; i++) {
            List<Integer> values = it.next();
            final String solStr = Arrays.toString(solution[i]);
            final String valStr = values.toString();
            assertEquals("i=" + i +
                    ", solution=" + solStr +
                    ", values=" + valStr,
                    solStr, valStr);
        }
    }
}
