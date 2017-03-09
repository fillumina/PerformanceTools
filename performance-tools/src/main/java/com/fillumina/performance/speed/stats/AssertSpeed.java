package com.fillumina.performance.speed.stats;

import com.fillumina.performance.assertion.AssertParameterized;
import com.fillumina.performance.assertion.AssertParameterizedSequence;
import com.fillumina.performance.assertion.AssertStats;
import com.fillumina.performance.assertion.Assertion;
import com.fillumina.performance.assertion.StatsAssertion;
import com.fillumina.performance.infrastructure.PHolder;
import com.fillumina.performance.template.ParameterizedMixedAssertion;
import com.fillumina.performance.util.stats.Ratio;

/**
 * It's a factory for speed related assertions.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AssertSpeed {

    public static AssertParameterizedSequence
                <Assertion<PHolder<PHolder<SpeedStats>>>, SpeedStats>
            parameterizedSequence() {
        return new AssertParameterizedSequence<>();
    }

    public static AssertParameterized
                <Assertion<PHolder<SpeedStats>>, SpeedStats>
            parameterized() {
        return new AssertParameterized<>();
    }

    public static StatsAssertion<ParameterizedMixedAssertion,SpeedStats>
            withTolerance(final Ratio tolerance) {
        return new AssertStats<ParameterizedMixedAssertion,SpeedStats>()
                .setTolerance(tolerance);
    }
}
