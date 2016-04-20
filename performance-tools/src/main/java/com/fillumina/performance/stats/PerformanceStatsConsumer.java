package com.fillumina.performance.stats;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface PerformanceStatsConsumer {

    void consume(final String message, final PerformanceStats performances);
}
