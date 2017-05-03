package com.fillumina.performance.infrastructure;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.util.TName;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class PerformanceConsumerExecutionChecker<A extends Assertable>
        implements PerformanceConsumer<A> {

    private boolean notified = false;
    private A performance;

    @Override
    public void consume(TName tname, A performance) {
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
