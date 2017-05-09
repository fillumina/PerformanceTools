package com.fillumina.performance.infrastructure;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.util.TName;

/**
 * Consumes statistics.
 *
 * @param A assertable
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface PerformanceConsumer<A extends Assertable> {

    /** Consumes a named performance statistics. */
    // TODO remove testName
    void consume(TName testName, A assertable);
}
