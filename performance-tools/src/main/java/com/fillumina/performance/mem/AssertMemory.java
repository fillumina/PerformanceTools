package com.fillumina.performance.mem;

import com.fillumina.performance.assertion.AssertStats;
import com.fillumina.performance.assertion.StatsAssertion;
import com.fillumina.performance.util.stats.Ratio;

/**
 * It's a factory for speed related assertions.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AssertMemory {

    public static StatsAssertion<Void, MemStats> withTolerance(Ratio ratio) {
        return AssertStats.<MemStats>withTolerance(ratio);
    }

}
