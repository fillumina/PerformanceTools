package com.fillumina.performance.infrastructure;

import com.fillumina.performance.assertion.Assertable;

/**
 * Consumes statistics.
 *
 * @param A assertable
 * 
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface PerformanceConsumer<A extends Assertable> {

    /** Consumes a named performance statistics. */
    void consume(PHolder<A> performances);
}
