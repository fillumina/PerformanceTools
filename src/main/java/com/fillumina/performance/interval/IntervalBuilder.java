package com.fillumina.performance.interval;

import java.io.Serializable;
import java.util.Iterator;

/**
 *
 * @author fra
 */
public class IntervalBuilder<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private final BuildableIntervalIterator<T> iterator;
    private T start, last, step;

    public IntervalBuilder(final BuildableIntervalIterator<T> iterator) {
        this.iterator = iterator;
    }

    public IntervalBuilder<T> last(final T last) {
        this.last = last;
        return this;
    }

    public IntervalBuilder<T> start(final T start) {
        this.start = start;
        return this;
    }

    public IntervalBuilder<T> step(final T step) {
        this.step = step;
        return this;
    }

    public Iterator<T> iterator() {
        iterator.setFirst(start);
        iterator.setLast(last);
        iterator.setStep(step);
        return iterator;
    }

    public Iterable<T> iterable() {
        return new Iterable<T>() {

            @Override
            public Iterator<T> iterator() {
                return IntervalBuilder.this.iterator();
            }

        };
    }
}
