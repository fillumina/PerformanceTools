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
    public void consume(PerformanceHolder<A> holder) {
        this.performance = holder.getStats();
        notified = true;
    }

    public boolean isNotified() {
        return notified;
    }

    public A getReceivedPerformance() {
        return performance;
    }
}
