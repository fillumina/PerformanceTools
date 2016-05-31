package com.fillumina.performance.infrastructure;

import com.fillumina.performance.util.ComposedName;

/**
 * Consume performance statistics that are returned or notified by
 * {@link PerformanceProducer}s.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface PerformanceConsumer<A> {

    /** Consumes a named performance statistics. */
    void consume(final ComposedName message, final A performances);
}
