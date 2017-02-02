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

    protected abstract boolean isLessThan(final T x, final T upBoundary);

    /**
     * Use this formula:
     * <code>result = first + step * index</code>.
     * <br>
     * Thought more time-consuming than a simple addition it allows for smaller
     * error (i.e. in case of floating point numbers).
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
        return new InnerIterator(first, last, step);
    }

    public List<T> toList() {
        final ArrayList<T> list = new ArrayList<>();
        for (T t: this) {
            list.add(t);
        }
        list.trimToSize();
        return list;
    }

    private class InnerIterator implements Iterator<T> {
        private T current;
        private final T first, last, step;
        private int index = 1;

        public InnerIterator(T first, T last, T step) {
            this.first = first;
            this.last = last;
            this.step = step;
            this.current = first;
        }

        @Override
        public boolean hasNext() {
            return isLessThan(current, last);
        }

        @Override
        public T next() {
            T prev = current;
            current = calculateCurrent(first, step, index);
            index++;
            return prev;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported.");
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "from=" + first + ", to=" + last +
                ", step=" + step + '}';
    }
}
