package com.fillumina.performance.time;

import com.fillumina.performance.assertion.AssertStats;
import com.fillumina.performance.util.stats.Ratio;

/**
 * It's a factory for speed related assertions.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
@Deprecated // TODO use AssertStats directly
public class AssertSpeed {

    public static AssertStats withTolerance(Ratio tolerance) {
        return new AssertStats().tolerance(tolerance);
    }
}
