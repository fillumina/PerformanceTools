package com.fillumina.performance.util.collection;

import java.io.Serializable;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class DoubleLinkedIndexes implements Cloneable, Serializable {
    private static final long serialVersionUID = 1L;

    private static final int REMOVED = Integer.MAX_VALUE;
    private static final int END_OF_FREE_QUEUE = Integer.MIN_VALUE;

    private int first = -1;
    private int last = -1;
    private int free = -1;
    private int[] array; // i=prev i+1=next

    public DoubleLinkedIndexes(int size) {
        this.array = new int[size << 1];
    }

    public DoubleLinkedIndexes(DoubleLinkedIndexes clone) {
        this(clone.array, clone.first, clone.last, clone.free);
    }

    protected DoubleLinkedIndexes(int[] array, int first, int last, int free) {
        this.array = array.clone();
        this.first = first;
        this.last = last;
        this.free = free;
    }

    public int addFirst() {
        int idx = popNextFree();
        if (idx == -1) {
            return -1;
        }
        addAtFirst(idx);
        return idx;
    }

    public int addLast() {
        int idx = popNextFree();
        if (idx == -1) {
            return -1;
        }
        addAtLast(idx);
        return idx;
    }

    private void addAtLast(int idx) {
        if (isNotEmptyElseAdd(idx)) {
            array[idx] = last;
            array[idx + 1] = -1;
            array[last + 1] = idx;
            last = idx;
        }
    }

    private void addAtFirst(int idx) {
        if (isNotEmptyElseAdd(idx)) {
            array[idx] = -1;
            array[idx + 1] = first;
            array[first] = idx;
            first = idx;
        }
    }

    private boolean isNotEmptyElseAdd(int idx) {
        if (first == -1) {
            first = idx;
            last = idx;
            array[idx] = -1;
            array[idx + 1] = -1;
            return false;
        }
        return true;
    }

    public void remove(int idx) {
        final int next = array[idx + 1];
        final int prev = array[idx];
        if (prev != REMOVED) {
            if (next != -1) {
                array[next] = prev;
            } else {
                last = prev;
            }
            if (prev != -1) {
                array[prev + 1] = next;
            } else {
                first = next;
            }
            array[idx] = REMOVED;
            array[idx + 1] = free;
            free = idx;
        }
    }

    public boolean isRemoved(int idx) {
        return array[idx] == REMOVED;
    }

    public boolean isEmpty() {
        return !isFull();
    }

    public boolean isFull() {
        return free == END_OF_FREE_QUEUE;
    }

    private int popNextFree() {
        int result = free;
        if (result == END_OF_FREE_QUEUE) {
            return -1;
        }
        if (result < 0) {
            free = result - 2;
            result = -result - 1;
            if (result + 2 == array.length) {
                free = END_OF_FREE_QUEUE;
            }
        } else {
            free = array[result + 1];
        }
        return result;
    }

    public int getFirst() {
        return first;
    }

    public int getLast() {
        return last;
    }

    public int getNext(int idx) {
        if (idx == -1) {
            return first;
        }
        return array[idx + 1];
    }

    public int getPrev(int idx) {
        if (idx == -1) {
            return last;
        }
        return array[idx];
    }

    public void increaseSize(int newsize) {
        if ((newsize << 1) < array.length) {
            throw new IllegalArgumentException("size cannot decrease, actual=" +
                    array.length + ", required=" + newsize);
        }
        int[] newarray = new int[newsize << 1];
        System.arraycopy(array, 0, newarray, 0, array.length);
        this.array = newarray;
    }

    public class FastIterator {
        private int index = DoubleLinkedIndexes.this.first;
        private int current = -1;

        public boolean hasNext() {
            return index != -1;
        }

        public int current() {
            return current;
        }

        public int next() {
            current = index;
            index = array[index + 1];
            return current;
        }

        public boolean hasPrevious() {
            return array[index] != -1;
        }

        public int previous() {
            index = array[index];
            current = index;
            return index;
        }

        public void remove() {
            int tmp = array[index];
            DoubleLinkedIndexes.this.remove(index);
            index = tmp;
            current = index;
        }
    }

    public FastIterator fastIterator() {
        return new FastIterator();
    }

    @Override
    public DoubleLinkedIndexes clone() {
        return new DoubleLinkedIndexes(array, first, last, free);
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append('[');
        FastIterator it = fastIterator();
        while(it.hasNext()) {
            buf.append(it.next()).append(", ");
        }
        buf.replace(buf.length() - 2, buf.length(), "]");
        return buf.toString();
    }
}
