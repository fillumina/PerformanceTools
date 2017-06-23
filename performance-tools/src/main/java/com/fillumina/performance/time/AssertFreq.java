package com.fillumina.performance.time;

import com.fillumina.performance.assertion.AssertStats;
import com.fillumina.performance.time.stats.FreqStats;
import com.fillumina.performance.util.stats.Ratio;

/**
 * It's a factory for speed related assertions.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AssertFreq {

    public static <C> AssertStats<FreqStats> withTolerance(
            Ratio tolerance) {
        return new AssertStats<FreqStats>().tolerance(tolerance);
    }
}
