package com.fillumina.performance.time;

import com.fillumina.performance.executor.stats.StatsType;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public enum TimeStatsType implements StatsType {
    AVERAGE("Average time"),
    THROUGHPUT("Throughput");

    final String name;

    TimeStatsType(String value) {
        this.name = value;
    }

    @Override
    public String toString() {
        return name;
    }
}
