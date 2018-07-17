package com.fillumina.performance.mem;

import com.fillumina.performance.executor.stats.StatsType;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public enum MemStatsType implements StatsType {
    ALLOCATED("Allocated"),
    USED("Used");

    String name;

    MemStatsType(String value) {
        this.name = value;
    }

    @Override
    public String toString() {
        return name;
    }
}
