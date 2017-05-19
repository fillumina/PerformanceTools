package com.fillumina.performance.speed;

import com.fillumina.performance.assertion.AssertStats;
import com.fillumina.performance.assertion.StatsAssertion;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.util.stats.Ratio;

/**
 * It's a factory for speed related assertions.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AssertSpeed {

    public static <C> StatsAssertion<C,SpeedStats> withTolerance(
            Ratio tolerance) {
        return new AssertStats<C,SpeedStats>().setTolerance(tolerance);
    }
}
