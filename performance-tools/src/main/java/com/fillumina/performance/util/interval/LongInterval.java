package com.fillumina.performance.util.interval;

import java.io.Serializable;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class LongInterval
        extends AbstractBuildableInterval<Long>
        implements BuildableInterval<Long>, Serializable {
    private static final long serialVersionUID = 1L;

    public static IntervalBuilder<Long> cycleFor() {
        return new IntervalBuilder<>(
                new LongInterval());
    }

    private LongInterval() {}

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
