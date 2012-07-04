package com.fillumina.performance.interval;

import java.io.Serializable;
import java.util.Iterator;

/**
 *
 * @author fra
 */
public class LongIntervalIterator
        implements BuildableIntervalIterator<Long>, Serializable {
    private static final long serialVersionUID = 1L;

    private Long index, last, step;

    public static IntervalIteratorBuilder<Long> cycleFor() {
        return new IntervalIteratorBuilder<>(
                new LongIntervalIterator());
    }

    private LongIntervalIterator() {}

    @Override
    public void setIndex(Long index) {
        this.index = index;
    }

    @Override
    public void setLast(Long last) {
        this.last = last;
    }

    @Override
    public void setStep(Long step) {
        this.step = step;
    }

    @Override
    public boolean hasNext() {
        return index < last;
    }

    @Override
    public Long next() {
        return index += step;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
