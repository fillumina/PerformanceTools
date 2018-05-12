package com.fillumina.performance.util.collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
    public void shouldAddFirst() {
        DoubleLinkedIndexesImpl dli = new DoubleLinkedIndexesImpl();
        dli.addAtFirst(3);

        dli.assertFirst(3);
    }

    @Test
    public void shouldAddBeforeFirst() {
        DoubleLinkedIndexesImpl dli = new DoubleLinkedIndexesImpl();
        dli.addAtFirst(3);
        dli.addAtFirst(4);

        dli.assertFirst(4);
        dli.assertConsecutive(4, 3);
        dli.assertLast(3);
    }

    @Test
    public void shouldAddLast() {
        DoubleLinkedIndexesImpl dli = new DoubleLinkedIndexesImpl();
        dli.addAtLast(3);

        dli.assertLast(3);
    }

    @Test
    public void shouldAddBeforeLast() {
        DoubleLinkedIndexesImpl dli = new DoubleLinkedIndexesImpl();
        dli.addAtLast(3);
        dli.addAtLast(4);

        dli.assertLast(4);
        dli.assertConsecutive(3, 4);
        dli.assertFirst(3);
    }

    @Test
    public void shouldCreate() {
        DoubleLinkedIndexesImpl dli =
                new DoubleLinkedIndexesImpl(0, 1, 2, 3, 4, 5);
        for (int i=0; i<6; i++) {
            int next = dli.getNext(i - 1);
            assertEquals(i, next);
        }
    }

    @Test
    public void shouldRemoveInMiddle() {
        DoubleLinkedIndexesImpl dli =
                new DoubleLinkedIndexesImpl(0, 1, 2, 9, 3, 4, 5);
        dli.assertConsecutive(2, 9);
        dli.assertConsecutive(9, 3);

        dli.remove(9);

        dli.assertConsecutive(2, 3);
    }

    @Test
    public void shouldRemoveAtFirst() {
        DoubleLinkedIndexesImpl dli =
                new DoubleLinkedIndexesImpl(0, 1, 2, 3, 4, 5);
        dli.assertFirst(0);
        dli.assertConsecutive(0, 1);

        dli.remove(0);

        dli.assertFirst(1);
        dli.assertConsecutive(1, 2);
    }

    @Test
    public void shouldRemoveAtLast() {
        DoubleLinkedIndexesImpl dli =
                new DoubleLinkedIndexesImpl(0, 1, 2, 3, 4, 5);
        dli.assertLast(5);
        dli.assertConsecutive(4, 5);

        dli.remove(5);

        dli.assertLast(4);
        dli.assertConsecutive(3, 4);
    }

    @Test
    public void shouldPopFreeIndexes() {
        DoubleLinkedIndexesImpl dli = new DoubleLinkedIndexesImpl();

        for (int i=0; i<10; i++) {
            assertEquals(i, dli.popNextFree());
        }

        assertEquals(-1, dli.popNextFree());
    }

    @Test
    public void shouldFinishPopFreeIndexes() {
        DoubleLinkedIndexesImpl dli = new DoubleLinkedIndexesImpl();

        for (int i=0; i<10; i++) {
            assertEquals(i, dli.popNextFree());
        }

        assertEquals(-1, dli.popNextFree());
    }

    @Test
    public void shouldPopFreeIndexesAfterRemoveMiddle() {
        DoubleLinkedIndexesImpl dli = new DoubleLinkedIndexesImpl();

        for (int i=0; i<10; i++) {
            assertEquals(i, dli.popNextFree());
        }

        dli.remove(4);
        dli.remove(7);

        assertEquals(7, dli.popNextFree());
        assertEquals(4, dli.popNextFree());
        assertEquals(-1, dli.popNextFree());
    }

    @Test
    public void shouldPopFreeIndexesAfterRemoveLast() {
        DoubleLinkedIndexesImpl dli = new DoubleLinkedIndexesImpl();

        for (int i=0; i<10; i++) {
            assertEquals(i, dli.popNextFree());
        }

        dli.remove(9);
        assertEquals(9, dli.popNextFree());
        assertEquals(-1, dli.popNextFree());
    }

    @Test
    public void shouldPopFreeIndexesAfterRemoveFirst() {
        DoubleLinkedIndexesImpl dli = new DoubleLinkedIndexesImpl();

        for (int i=0; i<10; i++) {
            assertEquals(i, dli.popNextFree());
        }

        // remove first
        dli.remove(0);
        assertEquals(0, dli.popNextFree());
        assertEquals(-1, dli.popNextFree());
    }

    @Test
    public void shouldReportFullness() {
        DoubleLinkedIndexesImpl dli = new DoubleLinkedIndexesImpl();

        assertFalse(dli.isFull());

        for (int i=0; i<10; i++) {
            assertEquals(i, dli.popNextFree());
        }

        assertTrue(dli.isFull());
    }

    @Test
    public void shouldReportEmptyIfRemovedElement() {
        DoubleLinkedIndexesImpl dli = new DoubleLinkedIndexesImpl();

        for (int i=0; i<10; i++) {
            assertEquals(i, dli.popNextFree());
        }

        dli.remove(5);
        assertFalse(dli.isFull());
    }

    @Test
    public void shouldRemoveWhileNotFilled() {
        DoubleLinkedIndexesImpl dli = new DoubleLinkedIndexesImpl();

        assertEquals(0, dli.popNextFree());
        assertEquals(1, dli.popNextFree());
        assertEquals(2, dli.popNextFree());
        assertEquals(3, dli.popNextFree());

        dli.remove(2);

        assertEquals(2, dli.popNextFree());
        assertEquals(4, dli.popNextFree());
    }

    @Test
    public void shouldPrintConsecutiveIndexesAddedAtFirst() {
        DoubleLinkedIndexesImpl dli = new DoubleLinkedIndexesImpl();
        dli.addFirst();
        dli.addFirst();
        dli.addFirst();
        dli.addFirst();

        assertEquals("[3, 2, 1, 0]", dli.toString());
    }

    @Test
    public void shouldPrintConsecutiveIndexesAddedAtLast() {
        DoubleLinkedIndexesImpl dli = new DoubleLinkedIndexesImpl();
        dli.addLast();
        dli.addLast();
        dli.addLast();
        dli.addLast();

        assertEquals("[0, 1, 2, 3]", dli.toString());
    }

    private static class DoubleLinkedIndexesImpl extends DoubleLinkedIndexes {
        private static final long serialVersionUID = 1L;

        DoubleLinkedIndexesImpl(int... array) {
            super(Math.max(array.length, 10));
            for (int a : array) {
                addAtLast(a);
            }
        }

        DoubleLinkedIndexesImpl(DoubleLinkedIndexesImpl clone) {
            super(clone);
        }

        @Override
        public int addFirst() {
            return super.addFirst() >> 1;
        }

        @Override
        public int addLast() {
            return super.addLast() >> 1;
        }

        @Override
        void addAtLast(int index) {
            super.addAtLast(index << 1);
        }

        @Override
        void addAtFirst(int index) {
            super.addAtFirst(index << 1);
        }

        @Override
        public void remove(int index) {
            super.remove(index << 1);
        }

        @Override
        boolean isRemoved(int index) {
            return super.isRemoved(index << 1);
        }

        @Override
        int popNextFree() {
            return super.popNextFree() >> 1;
        }

        @Override
        int getFirst() {
            return super.getFirst() >> 1;
        }

        @Override
        int getLast() {
            return super.getLast() >> 1;
        }

        @Override
        int getNext(int index) {
            final int next = super.getNext(index == -1 ? -1: index << 1);
            return next == -1 ? -1 : next >> 1;
        }

        @Override
        int getPrev(int index) {
            final int prev = super.getPrev(index == -1 ? -1 : index << 1);
            return prev == -1 ? -1 : prev >> 1;
        }

        @Override
        public FastIterator fastIterator() {
            return new FastIterator() {
                FastIterator it = DoubleLinkedIndexesImpl.super.fastIterator();

                @Override
                public boolean hasNext() {
                    return it.hasNext();
                }

                @Override
                public int next() {
                    return it.next() >> 1;
                }

                @Override
                public boolean hasPrevious() {
                    return it.hasPrevious();
                }

                @Override
                public int previous() {
                    return it.previous() >> 1;
                }

                @Override
                public void remove() {
                    it.remove();
                }
            };
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
