package com.fillumina.performance.mem;

import com.fillumina.performance.executor.stats.Stats;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public enum MemStatsType implements Stats.Type {
    ALLOCATED("Allocated"),
    USED("Used");

    private String name;

    MemStatsType(String value) {
        this.name = value;
    }

    @Override
    public String toString() {
        return name;
    }
}
