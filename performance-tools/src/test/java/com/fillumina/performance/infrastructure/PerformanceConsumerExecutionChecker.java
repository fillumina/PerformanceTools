package com.fillumina.performance.infrastructure;

import com.fillumina.performance.util.ComposedName;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class PerformanceConsumerExecutionChecker<A>
        implements PerformanceConsumer<A> {

    private boolean notified = false;
    private A performance;

    @Override
    public void consume(final ComposedName message, final A performance) {
        this.performance = performance;
        notified = true;
    }

    public boolean isNotified() {
        return notified;
    }

    public A getReceivedPerformance() {
        return performance;
    }
}
