package com.fillumina.performance.time;

import com.fillumina.performance.assertion.AssertStats;
import com.fillumina.performance.time.stats.ThroughputStats;
import com.fillumina.performance.util.stats.Ratio;

/**
 * It's a factory for speed related assertions.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AssertFreq {

    public static <C> AssertStats<ThroughputStats> withTolerance(
            Ratio tolerance) {
        return new AssertStats<ThroughputStats>().tolerance(tolerance);
    }
}
