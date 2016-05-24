package com.fillumina.performance.infrastructure;

import com.fillumina.performance.util.ComposedName;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface PerformanceFormatter<A> {

    String toString(A performance);

    String toString(ComposedName name, A performance);
}
