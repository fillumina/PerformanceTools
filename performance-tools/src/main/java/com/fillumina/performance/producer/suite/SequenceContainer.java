package com.fillumina.performance.producer.suite;

import java.util.Iterator;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface SequenceContainer<T extends SequenceContainer<T,S>, S> {

    T setSequence(final S... sequence);

    T setSequence(final Iterable<S> iterable);

    T setSequence(final Iterator<S> iterator);

    /**
     * Allows to define a proper name for each element of the sequence.
     * By default the name is given by the string representation of the
     * element.
     */
    T setSequenceNominator(final SequenceNominator<S> namedSequence);
}
