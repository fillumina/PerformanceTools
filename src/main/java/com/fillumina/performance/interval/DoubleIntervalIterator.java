package com.fillumina.performance.interval;

import java.io.Serializable;

/**
 *
 * @author fra
 */
public class DoubleIntervalIterator
        extends AbstractBuildableIntervalIterator<Double>
        implements BuildableIntervalIterator<Double>, Serializable {
    private static final long serialVersionUID = 1L;

    public static IntervalBuilder<Double> cycleFor() {
        return new IntervalBuilder<>(
                new DoubleIntervalIterator());
    }

    private DoubleIntervalIterator() {}

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
