package com.fillumina.performance.util.interval;

import java.io.Serializable;

/**
 *
 * @author Francesco Illuminati
 */
public class DoubleInterval
        extends AbstractIterableBuilder<Double>
        implements Serializable {
    private static final long serialVersionUID = 1L;

    public static IntervalBuilder<Double> from(Double start) {
        return new IntervalBuilder<>(new DoubleInterval(), start);
    }

    private DoubleInterval() {}

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
