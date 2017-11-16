package com.fillumina.performance.util.sequence;

import java.io.Serializable;

/**
 *
 * @author Francesco Illuminati
 */
public class DoubleSequence
        extends AbstractIterableBuilder<Double>
        implements Serializable {
    private static final long serialVersionUID = 1L;

    public static SequenceBuilder<Double> from(Double start) {
        return new SequenceBuilder<>(new DoubleSequence(), start);
    }

    private DoubleSequence() {}

    @Override
    protected boolean isLessOrEqualThan(
            Double smaller, Double bigger, Double step, boolean inclusive) {
        return inclusive ?
                Double.compare(smaller, bigger + step / 2) < 1 :
                Double.compare(smaller, bigger - step / 2) < 1;
    }

    @Override
    protected Double calculateCurrent(final Double first,
            final Double step, final int index) {
        return first + index * step;
    }
}
