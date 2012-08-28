package com.fillumina.performance.interval;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 *
 * @author fra
 */
public class DecimalIntervalIterator
        extends AbstractBuildableIntervalIterator<BigDecimal>
        implements BuildableIntervalIterator<BigDecimal>, Serializable {
    private static final long serialVersionUID = 1L;

    public static IntervalBuilder<BigDecimal> cycleFor() {
        return new IntervalBuilder<>(
                new DecimalIntervalIterator());
    }

    private DecimalIntervalIterator() {}

    @Override
    protected boolean isLessThan(final BigDecimal smaller, final BigDecimal bigger) {
        return smaller.compareTo(bigger) == -1;
    }

    @Override
    protected BigDecimal add(final BigDecimal base, final BigDecimal addendum) {
        return base.add(addendum);
    }
}
