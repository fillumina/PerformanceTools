package com.fillumina.performance.util.interval;

import java.io.Serializable;

/**
 *
 * @author Francesco Illuminati
 */
public class IntegerInterval
        extends AbstractIterableBuilder<Integer>
        implements Serializable {
    private static final long serialVersionUID = 1L;

    public static IntervalBuilder<Integer> from(Integer start) {
        return new IntervalBuilder<>(new IntegerInterval(), start);
    }

    private IntegerInterval() {}

    @Override
    protected boolean isLessThan(final Integer smaller, final Integer bigger) {
        return smaller < bigger;
    }

    @Override
    protected Integer calculateCurrent(final Integer first,
            final Integer step, final int index) {
        return first + index * step;
    }
}
