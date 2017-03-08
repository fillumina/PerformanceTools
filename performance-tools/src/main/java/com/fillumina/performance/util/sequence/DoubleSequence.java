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
    protected boolean isLessThan(final Double smaller, final Double bigger) {
        return Double.compare(smaller, bigger) == -1;
    }

    @Override
    protected Double calculateCurrent(final Double first,
            final Double step, final int index) {
        return first + index * step;
    }
}
