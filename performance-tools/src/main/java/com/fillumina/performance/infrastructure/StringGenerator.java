package com.fillumina.performance.infrastructure;

import com.fillumina.performance.assertion.AssertableMultiStats;

/**
 * Returns a String representation of the given object.
 *
 * @param A test type
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface StringGenerator<A extends AssertableMultiStats> {

    /** @return a String representation for the given object. */
    String toString(PerformanceHolder<A> t);
}
