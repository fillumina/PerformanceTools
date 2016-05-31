package com.fillumina.performance.infrastructure;

import com.fillumina.performance.util.ComposedName;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ConsumerExecutionChecker<A>
        implements PerformanceConsumer<A> {

    private boolean called = false;
    private A performance;

    @Override
    public void consume(final ComposedName message, final A performance) {
        this.performance = performance;
        called = true;
    }

    public boolean isCalled() {
        return called;
    }

    public A getReceivedPerformance() {
        return performance;
    }
}
