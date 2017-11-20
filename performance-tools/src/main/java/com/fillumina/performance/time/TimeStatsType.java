package com.fillumina.performance.time;

import com.fillumina.performance.executor.stats.Stats;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public enum TimeStatsType implements Stats.Type {
    AVERAGE_TIME("Average time"),
    THROUGHPUT("Throughput");

    private final String name;

    TimeStatsType(String value) {
        this.name = value;
    }

    @Override
    public String toString() {
        return name;
    }
}
