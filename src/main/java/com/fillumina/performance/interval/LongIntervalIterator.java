package com.fillumina.performance.interval;

import java.io.Serializable;

/**
 *
 * @author fra
 */
public class LongIntervalIterator
        extends AbstractBuildableIntervalIterator<Long>
        implements BuildableIntervalIterator<Long>, Serializable {
    private static final long serialVersionUID = 1L;

    public static IntervalBuilder<Long> cycleFor() {
        return new IntervalBuilder<>(
                new LongIntervalIterator());
    }

    private LongIntervalIterator() {}

    @Override
    protected boolean isLessThan(final Long smaller, final Long bigger) {
        return smaller < bigger;
    }

    @Override
    protected Long calculateCurrent(final Long first,
            final Long step, final int index) {
        return first + index * step;
    }
}
