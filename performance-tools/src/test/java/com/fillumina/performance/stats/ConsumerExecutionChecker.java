package com.fillumina.performance.stats;

import com.fillumina.performance.stats.PerformanceStats;
import com.fillumina.performance.stats.PerformanceStatsConsumer;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ConsumerExecutionChecker implements PerformanceStatsConsumer {

    private boolean called = false;

    @Override
    public void consume(final String message, final PerformanceStats stats) {
        called = true;
    }

    public boolean isCalled() {
        return called;
    }
}
