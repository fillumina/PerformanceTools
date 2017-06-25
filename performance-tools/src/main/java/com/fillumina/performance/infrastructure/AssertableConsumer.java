package com.fillumina.performance.infrastructure;

import com.fillumina.performance.assertion.Assertable;

/**
 * Consumes statistics.
 *
 * @param A assertable
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface AssertableConsumer<A extends Assertable> {

    /** Consumes an {@link Assertable}. */
    void consume(A assertable);
}
