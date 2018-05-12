package com.fillumina.performance.util.collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class DoubleLinkedIndexesTest {

    @Test
    public void shouldReturnFirstIfEmtpy() {
        DoubleLinkedIndexesImpl dli = new DoubleLinkedIndexesImpl();
        dli.assertFirst(-1);
    }

    @Test
    public void shouldReturnLasetIfEmtpy() {
        DoubleLinkedIndexesImpl dli = new DoubleLinkedIndexesImpl();
        dli.assertLast(-1);
    }

    @Test
    public void shouldFillUpOrderly() {
        DoubleLinkedIndexesImpl dli = new DoubleLinkedIndexesImpl(10);
        for (int i=0; i<10; i++) {
            assertEquals(i << 1, dli.addLast());
        }
    }

    @Test
    public void shoulAddConsecutively() {
        DoubleLinkedIndexesImpl dli = new DoubleLinkedIndexesImpl(10);
        for (int i=0; i<10; i++) {
            assertEquals(i << 1, dli.addLast());
        }
        dli.assertFirst(0);
        dli.assertLast(18);
        for (int i=0; i<18; i+=2) {
            dli.assertConsecutive(i, i+2);
        }
    }

    @Test
    public void shouldRemoveAtFirst() {
        DoubleLinkedIndexesImpl dli = new DoubleLinkedIndexesImpl(5);
        dli.add(5);

        dli.remove(0);

        dli.assertFirst(2);
        dli.assertConsecutive(2, 4);
    }

    @Test
    public void shouldRemoveAtLast() {
        DoubleLinkedIndexesImpl dli = new DoubleLinkedIndexesImpl(5);
        dli.add(5);

        dli.remove(8);

        dli.assertLast(6);
        dli.assertConsecutive(4, 6);
    }

    @Test
    public void shouldRemoveInMiddle() {
        DoubleLinkedIndexesImpl dli = new DoubleLinkedIndexesImpl(5);
        dli.add(5);

        dli.remove(2);

        dli.assertConsecutive(0, 4);
    }

    @Test
    public void shouldContinueToFilleAfterRemovingInMiddle() {
        DoubleLinkedIndexesImpl dli = new DoubleLinkedIndexesImpl(8);
        assertEquals(0, dli.addLast());
        assertEquals(2, dli.addLast());
        assertEquals(4, dli.addLast());
        assertEquals(6, dli.addLast());

        dli.remove(2);

        assertEquals(2, dli.addLast());
        assertEquals(8, dli.addLast());
    }

    @Test
    public void shouldContinueToFilleAfterRemoving2InMiddle() {
        DoubleLinkedIndexesImpl dli = new DoubleLinkedIndexesImpl(8);
        assertEquals(0, dli.addLast());
        assertEquals(2, dli.addLast());
        assertEquals(4, dli.addLast());
        assertEquals(6, dli.addLast());

        dli.remove(2);
        dli.remove(4);

        assertEquals(4, dli.addLast());
        assertEquals(2, dli.addLast());
        assertEquals(8, dli.addLast());
    }

    @Test
    public void shouldReportNoMoreSpace() {
        DoubleLinkedIndexesImpl dli = new DoubleLinkedIndexesImpl(4);
        assertEquals(0, dli.addLast());
        assertEquals(2, dli.addLast());
        assertEquals(4, dli.addLast());
        assertEquals(6, dli.addLast());

        assertEquals(-1, dli.addLast());
        assertTrue(dli.isFull());
    }

    @Test
    public void shouldPrintConsecutiveIndexesAddedAtFirst() {
        DoubleLinkedIndexesImpl dli = new DoubleLinkedIndexesImpl();
        dli.addFirst();
        dli.addFirst();
        dli.addFirst();
        dli.addFirst();

        assertEquals("[6, 4, 2, 0]", dli.toString());
    }

    @Test
    public void shouldPrintConsecutiveIndexesAddedAtLast() {
        DoubleLinkedIndexesImpl dli = new DoubleLinkedIndexesImpl();
        dli.addLast();
        dli.addLast();
        dli.addLast();
        dli.addLast();

        assertEquals("[0, 2, 4, 6]", dli.toString());
    }

    private static class DoubleLinkedIndexesImpl extends DoubleLinkedIndexes {
        private static final long serialVersionUID = 1L;

        DoubleLinkedIndexesImpl() {
            super(10);
        }

        DoubleLinkedIndexesImpl(int size) {
            super(size);
        }

        DoubleLinkedIndexesImpl(DoubleLinkedIndexesImpl clone) {
            super(clone);
        }

        void add(int last) {
            for (int i=0; i<last; i++) {
                addLast();
            }
        }

        void assertConsecutive(int prev, int next) {
            assertEquals(next, getNext(prev));
            assertEquals(prev, getPrev(next));
        }

        void assertFirst(int index) {
            assertEquals(-1, getPrev(index));
            assertEquals(index, getFirst());
        }

        void assertLast(int index) {
            assertEquals(-1, getNext(index));
            assertEquals(index, getLast());
        }

        @Override
        public DoubleLinkedIndexesImpl clone() {
            return new DoubleLinkedIndexesImpl(this);
        }
    }
}
