package com.fillumina.performance.util.sequence;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 *
 * @author Francesco Illuminati
 */
public class DecimalSequence
        extends AbstractIterableBuilder<BigDecimal>
        implements Serializable {
    private static final long serialVersionUID = 1L;

    public static SequenceBuilder<BigDecimal> from(BigDecimal start) {
        return new SequenceBuilder<>(new DecimalSequence(), start);
    }

    private DecimalSequence() {}

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
