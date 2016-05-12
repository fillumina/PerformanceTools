package com.fillumina.performance.suite;

import java.util.Map;

/**
 *
 * @author Francesco Illuminati
 */
public interface SequenceContainer<S> {

    /** Defines a sequence directly. */
    SequenceContainer<S> setSequence(final S... sequence);

    /** Defines a sequence by an iterable (values are copied). */
    SequenceContainer<S> setSequence(final Iterable<S> iterable);

    /**
     * Defines a named sequence.
     *
     * @see com.fillumina.performance.util.Mapper
     */
    SequenceContainer<S> setSequence(final Map<String,S> namedSequence);

    /** Adds a single sequence item. */
    SequenceContainer<S> setSequenceItem(String name, S item);
}
