package com.fillumina.performance.stats;

import com.fillumina.performance.infrastructure.PerformanceConsumer;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ConsumerExecutionChecker
        implements PerformanceConsumer<PerformanceStats> {

    private boolean called = false;

    @Override
    public void consume(final String message, final PerformanceStats stats) {
        called = true;
    }

    public boolean isCalled() {
        return called;
    }
}
