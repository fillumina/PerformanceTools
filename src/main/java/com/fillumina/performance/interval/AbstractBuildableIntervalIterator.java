package com.fillumina.performance.interval;

import java.io.Serializable;

/**
 *
 * @author fra
 */
public abstract class AbstractBuildableIntervalIterator<T>
        implements BuildableIntervalIterator<T>, Serializable {
    private static final long serialVersionUID = 1L;

    protected T first, index, last, step;

    protected abstract boolean isLessThan(final T smaller, final T bigger);
    protected abstract T add(final T base, final T addendum);

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
        return index == null || isLessThan(index, last);
    }

    @Override
    public T next() {
        return index = (index == null) ? first : add(index, step);
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
