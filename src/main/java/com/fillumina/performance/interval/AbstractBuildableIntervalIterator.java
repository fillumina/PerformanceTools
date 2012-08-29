package com.fillumina.performance.interval;

import java.io.Serializable;

/**
 *
 * @author fra
 */
public abstract class AbstractBuildableIntervalIterator<T>
        implements BuildableIntervalIterator<T>, Serializable {
    private static final long serialVersionUID = 1L;

    protected T first, last, step, current;
    private int index;

    protected abstract boolean isLessThan(final T smaller, final T bigger);
    protected abstract T calculateCurrent(final T first,
            final T step, final int index);

    @Override
    public void setFirst(final T first) {
        this.first = first;
    }

    @Override
    public void setLast(final T last) {
        this.last = last;
    }

    @Override
    public void setStep(final T step) {
        this.step = step;
    }

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
