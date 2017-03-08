package com.fillumina.performance.util.sequence;

import java.io.Serializable;

/**
 *
 * @author Francesco Illuminati
 */
public class LongSequence
        extends AbstractIterableBuilder<Long>
        implements Serializable {
    private static final long serialVersionUID = 1L;

    public static SequenceBuilder<Long> from(Long start) {
        return new SequenceBuilder<>(new LongSequence(), start);
    }

    private LongSequence() {}

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
