package com.fillumina.performance.util.sequence;

import java.io.Serializable;

/**
 *
 * @author Francesco Illuminati
 */
public class SequenceBuilder<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private final AbstractIterableBuilder<T> iterable;


    public SequenceBuilder(final AbstractIterableBuilder<T> iterator, T start) {
        this.iterable = iterator;
        this.iterable.setFirst(start);
    }

    public IntervalBuilderStep to(final T last) {
        this.iterable.setLast(last);
        return new IntervalBuilderStep();
    }

    /**
     * Uses telescopic classes so it's impossible to miss an initialization
     * parameter.
     */
    public class IntervalBuilderStep {

        public AbstractIterableBuilder<T> step(final T step) {
            iterable.setStep(step);
            return iterable;
        }
    }
}
