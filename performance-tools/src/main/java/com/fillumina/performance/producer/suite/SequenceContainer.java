package com.fillumina.performance.producer.suite;

import java.util.Iterator;

/**
 *
 * @author fra
 */
public interface SequenceContainer<T extends SequenceContainer<T,S>, S> {

    T setSequence(final S... sequence);

    T setSequence(final Iterable<S> iterable);

    T setSequence(final Iterator<S> iterator);
}
