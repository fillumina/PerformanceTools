package com.fillumina.performance.mock;

import com.fillumina.performance.executor.stats.StatsType;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MockStatsType implements StatsType {

    public static final MockStatsType INSTANCE = new MockStatsType("Mock Stats");

    final String name;

    public MockStatsType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
