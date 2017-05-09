package com.fillumina.performance.infrastructure;

import com.fillumina.performance.assertion.Assertable;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class PerformanceConsumerExecutionChecker<A extends Assertable>
        implements PerformanceConsumer<A> {

    private boolean notified = false;
    private A performance;

    @Override
    public void consume(A assertable) {
        this.performance = assertable;
        notified = true;
    }

    public boolean isNotified() {
        return notified;
    }

    public A getReceivedAssertable() {
        return performance;
    }
}
