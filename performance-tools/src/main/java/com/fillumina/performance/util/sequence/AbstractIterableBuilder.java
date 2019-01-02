package com.fillumina.performance.util.sequence;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 *
 * @author Francesco Illuminati
 */
public abstract class AbstractIterableBuilder<T>
        implements Iterable<T>, Serializable {
    private static final long serialVersionUID = 1L;

    private T first, last, step;
    private boolean inclusive = true; // default

    protected abstract boolean isLessOrEqualThan(
            T x, T upBoundary, T step, boolean inclusive);

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

    void setInclusive(boolean inclusive) {
        this.inclusive = inclusive;
    }

    @Override
    public Iterator<T> iterator() {
        return new InnerIterator(first, last, step, inclusive);
    }

    public List<T> toList() {
        final ArrayList<T> list = new ArrayList<>();
        for (T t: this) {
            list.add(t);
        }
        list.trimToSize();
        return list;
    }

    public Stream<T> toStream() {
        return StreamSupport.stream(
            Spliterators.spliteratorUnknownSize(iterator(), Spliterator.ORDERED),
            false);
    }

    private class InnerIterator implements Iterator<T> {
        private T current;
        private final T first, last, step;
        private int index = 1;
        private boolean inclusive;

        public InnerIterator(T first, T last, T step, boolean inclusive) {
            this.first = first;
            this.last = last;
            this.step = step;
            this.current = first;
            this.inclusive = inclusive;
        }

        @Override
        public boolean hasNext() {
            return isLessOrEqualThan(current, last, step, inclusive);
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
