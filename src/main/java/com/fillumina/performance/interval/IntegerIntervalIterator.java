package com.fillumina.performance.interval;

import java.io.Serializable;

/**
 *
 * @author fra
 */
public class IntegerIntervalIterator
        extends AbstractBuildableIntervalIterator<Integer>
        implements BuildableIntervalIterator<Integer>, Serializable {
    private static final long serialVersionUID = 1L;

    public static IntervalBuilder<Integer> cycleFor() {
        return new IntervalBuilder<>(
                new IntegerIntervalIterator());
    }

    private IntegerIntervalIterator() {}

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
