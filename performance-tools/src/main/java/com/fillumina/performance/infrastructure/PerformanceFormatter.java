package com.fillumina.performance.infrastructure;

import com.fillumina.performance.util.ComposedName;

/**
 * Return a String representation of the given performances.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface PerformanceFormatter<A> {

    /** @return a String representation for the given performances. */
    String toString(A performance);

    /** @return a String representation for the given named performances. */
    String toString(ComposedName name, A performance);
}
