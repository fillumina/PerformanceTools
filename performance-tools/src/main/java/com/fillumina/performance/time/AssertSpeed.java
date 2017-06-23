package com.fillumina.performance.time;

import com.fillumina.performance.assertion.AssertStats;
import com.fillumina.performance.time.stats.TimeStats;
import com.fillumina.performance.util.stats.Ratio;

/**
 * It's a factory for speed related assertions.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AssertSpeed {

    public static <C> AssertStats<TimeStats> withTolerance(
            Ratio tolerance) {
        return new AssertStats<TimeStats>().tolerance(tolerance);
    }
}
