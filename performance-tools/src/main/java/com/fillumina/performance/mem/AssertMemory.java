package com.fillumina.performance.mem;

import com.fillumina.performance.assertion.AssertStats;
import com.fillumina.performance.util.stats.Ratio;

/**
 * It's a factory for speed related assertions.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
@Deprecated // TODO remove, use AssertStats directly
public class AssertMemory {

    public static AssertStats withTolerance(Ratio ratio) {
        return AssertStats.withTolerance(ratio);
    }

}
