package com.fillumina.performance.util;

import java.util.Iterator;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class CountingIterator implements Iterator<Integer>, Iterable<Integer> {

    private final int start;
    private final int end;
    private final int step;
    private int counter = 0;

    public CountingIterator() {
        this(0, 1 << 31, 1);
    }

    public CountingIterator(int end) {
        this(0, end, 1);
    }

    public CountingIterator(int start, int end) {
        this(start, end, 1);
    }

    public CountingIterator(int start, int end, int step) {
        this.start = start;
        this.end = end;
        this.step = step;
        this.counter = start;
    }

    /**
     * Cannot be reused. Used mainly in for... iterations:
     *
     * <pre>
     * for (int i : new CountingIterator(18)) { ... }
     * </pre>
     *
     * @return
     */
    @Override
    public Iterator<Integer> iterator() {
        return this;
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public Integer next() {
        counter += step;
        if (counter > end) {
            counter = start;
        }
        return counter;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
