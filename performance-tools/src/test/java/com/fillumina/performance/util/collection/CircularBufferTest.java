package com.fillumina.performance.util.collection;

import java.util.Iterator;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class CircularBufferTest {

    @Test
    public void shouldReturnEmpty() {
        CircularBuffer<Integer> history = new CircularBuffer<>(3);
        assertTrue(history.isEmpty());
    }

    @Test
    public void shouldReturnNotEmpty() {
        CircularBuffer<Integer> history = new CircularBuffer<>(3);
        history.add(0);

        assertFalse(history.isEmpty());
    }

    @Test
    public void shouldHistoryBeIterable() {
        CircularBuffer<Integer> history = new CircularBuffer<>(3);
        history.add(0);
        history.add(1);
        history.add(2);
        history.add(3);
        history.add(4);
        history.add(5);

        Iterator<Integer> it = history.iterator();

        assertTrue(it.hasNext());
        assertEquals(3, it.next(), 0);

        assertTrue(it.hasNext());
        assertEquals(4, it.next(), 0);

        assertTrue(it.hasNext());
        assertEquals(5, it.next(), 0);

        assertFalse(it.hasNext());
    }

    @Test
    public void shouldGetFromFullHistory() {
        CircularBuffer<Integer> history = new CircularBuffer<>(4);
        history.add(0);
        history.add(1);
        history.add(2);
        history.add(3);
        history.add(4);
        history.add(5);

        assertEquals(2, history.get(0), 0);
        assertEquals(3, history.get(1), 0);
        assertEquals(4, history.get(2), 0);
        assertEquals(5, history.get(3), 0);
    }

    @Test
    public void shouldGetFromNonFullHistory() {
        CircularBuffer<Integer> history = new CircularBuffer<>(5);
        history.add(0);
        history.add(1);
        history.add(2);

        assertEquals(0, history.get(0), 0);
        assertEquals(1, history.get(1), 0);
        assertEquals(2, history.get(2), 0);
    }

    @Test
    public void shouldHistoryBeIterableUnderTheFullSize() {
        CircularBuffer<Integer> history = new CircularBuffer<>(3);
        history.add(0);
        history.add(1);

        Iterator<Integer> it = history.iterator();

        assertTrue(it.hasNext());
        assertEquals(0, it.next(), 0);

        assertTrue(it.hasNext());
        assertEquals(1, it.next(), 0);

        assertFalse(it.hasNext());
    }

    @Test
    public void shouldGetLastEntered() {
        CircularBuffer<Integer> history = new CircularBuffer<>(5);
        history.add(0);
        history.add(1);
        history.add(2);

        assertEquals(2, history.getLastInserted(), 0);
    }

    @Test
    public void shouldGetOlderEntered() {
        CircularBuffer<Integer> history = new CircularBuffer<>(5);
        history.add(0);
        history.add(1);
        history.add(2);

        assertEquals(0, history.getOlderInserted(), 0);
    }

    @Test
    public void shouldGetOlderEnteredInFilledBuffer() {
        CircularBuffer<Integer> history = new CircularBuffer<>(3);
        history.add(0);
        history.add(1);
        history.add(2);
        history.add(3);

        assertEquals(1, history.getOlderInserted(), 0);
    }
}
