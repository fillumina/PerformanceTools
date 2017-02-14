package com.fillumina.performance.infrastructure;

import com.fillumina.performance.assertion.Assertable;

/**
 * Consume performance statistics that are returned or notified by
 * {@link PerformanceProducer}s.
 *
 * @param A type of statistics
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface PerformanceConsumer<A extends Assertable> {

    /** Consumes a named performance statistics. */
    void consume(PerformanceHolder<A> performances);
}
