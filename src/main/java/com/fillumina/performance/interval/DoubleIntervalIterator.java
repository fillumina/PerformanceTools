package com.fillumina.performance.interval;

import java.io.Serializable;

/**
 *
 * @author fra
 */
public class DoubleIntervalIterator
        implements BuildableIntervalIterator<Double>, Serializable {
    private static final long serialVersionUID = 1L;

    private Double index, step, last;

    public static IntervalIteratorBuilder<Double> cycleFor() {
        return new IntervalIteratorBuilder<>(
                new DoubleIntervalIterator());
    }

    private DoubleIntervalIterator() {}

    @Override
    public void setIndex(final Double index) {
        this.index = index;
    }

    @Override
    public void setLast(final Double last) {
        this.last = last;
    }

    @Override
    public void setStep(final Double step) {
        this.step = step;
    }

    @Override
    public boolean hasNext() {
        return index < last;
    }

    @Override
    public Double next() {
        return index += step;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
