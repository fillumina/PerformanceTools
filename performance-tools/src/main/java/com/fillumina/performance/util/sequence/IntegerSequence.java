package com.fillumina.performance.util.sequence;

import java.io.Serializable;

/**
 * Interval over {@link Integer}. The upper bound is exclusive so
 * the interval from 0 to 10 step 5 will return the sequence [0, 5].
 *
 * @author Francesco Illuminati
 */
public class IntegerSequence
        extends AbstractIterableBuilder<Integer>
        implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Starting point of the sequence. */
    public static SequenceBuilder<Integer> from(Integer start) {
        return new SequenceBuilder<>(new IntegerSequence(), start);
    }

    private IntegerSequence() {}

    @Override
    protected boolean isLessOrEqualThan(Integer smaller, Integer bigger,
            Integer step, boolean inclusive) {
        return inclusive ? smaller <= bigger : smaller < bigger;
    }

    @Override
    protected Integer calculateCurrent(final Integer first,
            final Integer step, final int index) {
        return first + index * step;
    }
}
