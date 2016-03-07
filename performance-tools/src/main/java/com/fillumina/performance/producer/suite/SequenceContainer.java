package com.fillumina.performance.producer.suite;

import java.util.Map;

/**
 *
 * @author Francesco Illuminati
 */
public interface SequenceContainer<T extends SequenceContainer<T,S>, S> {

    /** Defines a sequence directly. */
    T setSequence(final S... sequence);

    /** Defines a sequence by an iterable (values are copied). */
    T setSequence(final Iterable<S> iterable);

    /**
     * Defines a named sequence.
     *
     * @see com.fillumina.performance.util.Mapper
     */
    T setSequence(final Map<String,S> namedSequence);

    /** Adds a single sequence item. */
    T setSequenceItem(String name, S item);
}
