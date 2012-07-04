package com.fillumina.performance.utils;

import java.io.Serializable;

/**
 *
 * @author fra
 */
public class IntegerIntervalIterator
        implements BuildableIntervalIterator<Integer>, Serializable {
    private static final long serialVersionUID = 1L;

    private Integer index, last, step;

    public static IntervalIteratorBuilder<Integer> cycleFor() {
        return new IntervalIteratorBuilder<>(
                new IntegerIntervalIterator());
    }

    private IntegerIntervalIterator() {}

    @Override
    public void setIndex(final Integer index) {
        this.index = index;
    }

    @Override
    public void setLast(final Integer last) {
        this.last = last;
    }

    @Override
    public void setStep(final Integer step) {
        this.step = step;
    }

    @Override
    public boolean hasNext() {
        return index < last;
    }

    @Override
    public Integer next() {
        return index += step;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
