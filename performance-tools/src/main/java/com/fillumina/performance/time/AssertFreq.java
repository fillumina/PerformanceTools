package com.fillumina.performance.time;

import com.fillumina.performance.assertion.Assertions;
import com.fillumina.performance.util.stats.Ratio;

/**
 * It's a factory for speed related assertions.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
@Deprecated // TODO use Assertions directly
public class AssertFreq {

    public static Assertions withTolerance(Ratio tolerance) {
        return new Assertions().tolerance(tolerance);
    }
}
