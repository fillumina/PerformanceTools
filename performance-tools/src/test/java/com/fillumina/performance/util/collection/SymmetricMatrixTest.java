package com.fillumina.performance.util.collection;

import java.util.Collection;
import java.util.Set;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SymmetricMatrixTest {

    @Test
    public void shouldCalculateRightIndex() {
        assertEquals(0, SymmetricMatrix.index(0, 0));
        assertEquals(1, SymmetricMatrix.index(1, 0));
        assertEquals(2, SymmetricMatrix.index(1, 1));
        assertEquals(3, SymmetricMatrix.index(2, 0));
        assertEquals(4, SymmetricMatrix.index(2, 1));
        assertEquals(5, SymmetricMatrix.index(2, 2));
        assertEquals(6, SymmetricMatrix.index(3, 0));
        assertEquals(7, SymmetricMatrix.index(3, 1));
        assertEquals(8, SymmetricMatrix.index(3, 2));
        assertEquals(9, SymmetricMatrix.index(3, 3));
    }

    @Test
    public void shouldFillTheMap() {
        SymmetricMatrix<String,String> matrix =
                new SymmetricMatrix<>("one", "two", "three");
        matrix.putByIndex(0, 1, "12");
        matrix.putByIndex(0, 2, "13");
        matrix.putByIndex(2, 1, "32");
        matrix.putByIndex(0, 0, "11");
        matrix.putByIndex(1, 1, "22");
        matrix.putByIndex(2, 2, "33");

        Collection<String> values = matrix.values();
        assertEquals(6, values.size());

        assertTrue(values.contains("12"));
        assertTrue(values.contains("13"));
        assertTrue(values.contains("32"));
        assertTrue(values.contains("11"));
        assertTrue(values.contains("22"));
        assertTrue(values.contains("33"));
    }

    @Test
    public void shouldBeSymmetric() {
        SymmetricMatrix<String,String> matrix =
                new SymmetricMatrix<>("one", "two", "three");
        matrix.putByIndex(0, 1, "12");
        matrix.putByIndex(1, 0, "21");


        Collection<String> values = matrix.values();
        assertTrue(values.contains("21"));
        assertFalse(values.contains("12"));
    }

    @Test
    public void shouldGetTheValues() {
        SymmetricMatrix<String,String> matrix =
                new SymmetricMatrix<>("one", "two", "three");
        matrix.putByIndex(0, 1, "12");
        matrix.putByIndex(0, 2, "13");
        matrix.putByIndex(2, 1, "32");

        assertEquals("12", matrix.get("one", "two"));
        assertEquals("12", matrix.get("two", "one"));

        assertEquals("13", matrix.get("one", "three"));
        assertEquals("13", matrix.get("three", "one"));

        assertEquals("32", matrix.get("three", "two"));
        assertEquals("32", matrix.get("two", "three"));
    }

    @Test
    public void shouldReturnTheCollectionOfValues() {
        SymmetricMatrix<String,String> matrix =
                new SymmetricMatrix<>("one", "two", "three");
        matrix.put("one", "two", "12");
        matrix.put("one", "three", "13");
        matrix.put("three", "two", "32");

        Collection<String> values = matrix.values();
        assertTrue(values.contains("12"));
        assertTrue(values.contains("13"));
        assertTrue(values.contains("32"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExecptionIfInexistentMapping() {
        SymmetricMatrix<String,String> matrix =
                new SymmetricMatrix<>("one", "two", "three");
        matrix.put("one", "two", "12");
        matrix.put("one", "three", "13");
        matrix.put("three", "two", "32");

        matrix.get("inexistent", "one");
    }

    @Test
    public void shouldNotAllowAMapWithOnlyOneElement() {
        SymmetricMatrix<String,String> matrix =
                new SymmetricMatrix<>("one");

        matrix.putByIndex(0, 0, "00");

        assertEquals(1, matrix.size());
        assertEquals("00", matrix.get("one", "one"));
    }

    @Test
    public void shouldSetAMapWithTwoElementsByIndex() {
        SymmetricMatrix<String,String> matrix =
                new SymmetricMatrix<>("one", "two");
        matrix.putByIndex(0, 0, "00");
        matrix.putByIndex(0, 1, "01");
        matrix.putByIndex(1, 1, "11");
        assertEquals(3, matrix.values().size());
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void sholdNotXBeLessThanZero() {
        SymmetricMatrix<String,String> matrix =
                new SymmetricMatrix<>("one", "two");
        matrix.putByIndex(-1, 0, "12");
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void sholdNotyBeLessThanZero() {
        SymmetricMatrix<String,String> matrix =
                new SymmetricMatrix<>("one", "two");
        matrix.putByIndex(0, -4, "12");
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void sholdNotXBeGreaterThanSize() {
        SymmetricMatrix<String,String> matrix =
                new SymmetricMatrix<>("one", "two");
        matrix.putByIndex(5, 0, "12");
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void sholdNotYBeGreaterThanSize() {
        SymmetricMatrix<String,String> matrix =
                new SymmetricMatrix<>("one", "two");
        matrix.putByIndex(0, 5, "12");
    }

    @Test
    public void shouldReturnTheSetOfKeys() {
        SymmetricMatrix<String,String> matrix =
                new SymmetricMatrix<>("one", "two", "three");

        Set<String> keys = matrix.keySet();
        assertEquals(3, keys.size());
        assertTrue(keys.contains("one"));
        assertTrue(keys.contains("two"));
        assertTrue(keys.contains("three"));
    }
}
