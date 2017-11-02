package com.fillumina.performance.executor.stats.producer;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface StatsProgressionStatusListener {

    void acceptStatsProgressionStatus(StatsProgressionStatus status);

    StatsProgressionStatusListener NULL = (s) -> {};
}
