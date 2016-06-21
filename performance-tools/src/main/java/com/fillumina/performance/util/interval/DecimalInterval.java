package com.fillumina.performance.util.interval;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 *
 * @author Francesco Illuminati
 */
public class DecimalInterval
        extends AbstractIterableBuilder<BigDecimal>
        implements Serializable {
    private static final long serialVersionUID = 1L;

    public static IntervalBuilder<BigDecimal> from(BigDecimal start) {
        return new IntervalBuilder<>(new DecimalInterval(), start);
    }

    private DecimalInterval() {}

    @Override
    protected boolean isLessThan(final BigDecimal smaller,
            final BigDecimal bigger) {
        return smaller.compareTo(bigger) == -1;
    }

    @Override
    protected BigDecimal calculateCurrent(final BigDecimal first,
            final BigDecimal step, final int index) {
        return first.add(step.multiply(BigDecimal.valueOf(index)));
    }
}
