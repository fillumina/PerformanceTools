package com.fillumina.performance.mock;

import com.fillumina.performance.executor.stats.Stats;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MockStatsType implements Stats.Type {

    public static final MockStatsType INSTANCE = new MockStatsType("Mock Stats");

    private final String name;

    public MockStatsType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
