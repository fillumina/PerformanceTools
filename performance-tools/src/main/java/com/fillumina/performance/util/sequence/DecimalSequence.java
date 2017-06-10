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
    private static final BigDecimal TWO = BigDecimal.valueOf(2.0);

    public static SequenceBuilder<BigDecimal> from(BigDecimal start) {
        return new SequenceBuilder<>(new DecimalSequence(), start);
    }

    private DecimalSequence() {}

    @Override
    protected boolean isLessOrEqualThan(BigDecimal smaller,
            BigDecimal bigger, BigDecimal step) {
        return smaller.compareTo(bigger.add(step.divide(TWO))) < 1;
    }

    @Override
    protected BigDecimal calculateCurrent(final BigDecimal first,
            final BigDecimal step, final int index) {
        return first.add(step.multiply(BigDecimal.valueOf(index)));
    }
}
