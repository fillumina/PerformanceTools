package com.fillumina.performance.infrastructure;

import com.fillumina.performance.util.ComposedName;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface PerformanceConsumer<A> {

    void consume(final ComposedName message, final A performances);
}
