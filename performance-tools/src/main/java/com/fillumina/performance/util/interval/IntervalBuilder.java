package com.fillumina.performance.util.interval;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class IntervalBuilder<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private final BuildableInterval<T> iterator;
    private T start, last, step;

    public IntervalBuilder(final BuildableInterval<T> iterator) {
        this.iterator = iterator;
    }

    public IntervalBuilder<T> to(final T last) {
        this.last = last;
        return this;
    }

    public IntervalBuilder<T> from(final T start) {
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

    public List<T> toList() {
        final List<T> list = new ArrayList<>();
        for (T t: iterable()) {
            list.add(t);
        }
        return list;
    }
}
