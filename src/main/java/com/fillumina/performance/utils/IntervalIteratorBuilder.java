package com.fillumina.performance.utils;

import java.io.Serializable;

/**
 *
 * @author fra
 */
public class IntervalIteratorBuilder<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private final BuildableIntervalIterator<T> iterator;
    private T start, last, step;

    public IntervalIteratorBuilder(final BuildableIntervalIterator<T> iterator) {
        this.iterator = iterator;
    }

    public IntervalIteratorBuilder<T> last(final T last) {
        this.last = last;
        return this;
    }

    public IntervalIteratorBuilder<T> start(final T start) {
        this.start = start;
        return this;
    }

    public IntervalIteratorBuilder<T> step(final T step) {
        this.step = step;
        return this;
    }

    public BuildableIntervalIterator<T> build() {
        iterator.setIndex(start);
        iterator.setLast(last);
        iterator.setStep(step);
        return iterator;
    }
}
