package com.fillumina.performance.producer.suite;

import java.util.Iterator;

/**
 *
 * @author fra
 */
public interface SequencesContainer<T extends SequencesContainer<T,S>, S> {

    T addSequence(final S... sequence);

    T addSequence(final Iterable<S> iterable);

    T addSequence(final Iterator<S> iterator);
}
