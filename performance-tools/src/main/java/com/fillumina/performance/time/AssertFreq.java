package com.fillumina.performance.time;

import com.fillumina.performance.assertion.AssertionChecker;
import com.fillumina.performance.util.stats.Ratio;

/**
 * It's a factory for speed related assertions.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
@Deprecated // TODO use AssertionChecker directly
public class AssertFreq {

    public static AssertionChecker withTolerance(Ratio tolerance) {
        return new AssertionChecker().tolerance(tolerance);
    }
}
