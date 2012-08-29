package com.fillumina.performance.util.interval;

import java.io.Serializable;

/**
 *
 * @author fra
 */
public class IntegerInterval
        extends AbstractBuildableInterval<Integer>
        implements BuildableInterval<Integer>, Serializable {
    private static final long serialVersionUID = 1L;

    public static IntervalBuilder<Integer> cycleFor() {
        return new IntervalBuilder<>(
                new IntegerInterval());
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
