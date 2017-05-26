package com.fillumina.performance.speed;

import com.fillumina.performance.assertion.AssertStats;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.util.stats.Ratio;

/**
 * It's a factory for speed related assertions.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AssertSpeed {

    public static <C> AssertStats<SpeedStats> withTolerance(
            Ratio tolerance) {
        return new AssertStats<SpeedStats>().tolerance(tolerance);
    }
}
