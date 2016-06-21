package com.fillumina.performance.util.interval;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Francesco Illuminati
 */
public abstract class AbstractIterableBuilder<T>
        implements Iterable<T>, Serializable {
    private static final long serialVersionUID = 1L;

    private T first, last, step;
    private int index;

    protected abstract boolean isLessThan(final T smaller, final T bigger);

    /**
     * Use this formula:
     * <code>result = first + step * index</code>.
     * <br>
     * Thought more time-consuming than a simple addition it allows for less
     * errors (i.e. in case of floating point numbers).
     */
    protected abstract T calculateCurrent(final T first,
            final T step, final int index);

    void setFirst(T first) {
        this.first = first;
    }

    void setLast(T last) {
        this.last = last;
    }

    void setStep(T step) {
        this.step = step;
    }

    @Override
    public Iterator<T> iterator() {
        return new InnerIterator();
    }

    public List<T> toList() {
        final List<T> list = new ArrayList<>();
        for (T t: this) {
            list.add(t);
        }
        return list;
    }

    private class InnerIterator implements Iterator<T> {
        private T current;

        @Override
        public boolean hasNext() {
            return current == null || isLessThan(current, last);
        }

        @Override
        public T next() {
            current = (current == null) ?
                    first : calculateCurrent(first, step, index);
            index++;
            return current;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}
