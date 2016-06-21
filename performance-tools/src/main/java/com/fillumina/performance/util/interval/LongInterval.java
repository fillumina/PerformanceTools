package com.fillumina.performance.util.interval;

import java.io.Serializable;

/**
 *
 * @author Francesco Illuminati
 */
public class LongInterval
        extends AbstractIterableBuilder<Long>
        implements Serializable {
    private static final long serialVersionUID = 1L;

    public static IntervalBuilder<Long> from(Long start) {
        return new IntervalBuilder<>(new LongInterval(), start);
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
