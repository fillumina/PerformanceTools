package com.fillumina.performance.speed.stats;

import com.fillumina.performance.assertion.AssertPerformance;
import com.fillumina.performance.assertion.Assertion;
import com.fillumina.performance.assertion.ParameterizedAssertion;
import com.fillumina.performance.assertion.ParameterizedSequenceAssertion;
import com.fillumina.performance.assertion.StatsAssertion;
import com.fillumina.performance.infrastructure.PHolder;
import com.fillumina.performance.template.ParameterizedMixedAssertion;
import com.fillumina.performance.util.stats.Ratio;
import java.util.ArrayList;

/**
 * It's a factory for speed related assertions.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AssertSpeed {

    public static ParameterizedSequenceAssertion
                <Assertion<PHolder<PHolder<SpeedStats>>>, SpeedStats>
            parameterizedSequence() {
        return new ParameterizedSequenceAssertion<>();
    }

    public static ParameterizedAssertion
                <Assertion<PHolder<SpeedStats>>, SpeedStats>
            parameterized() {
        return new ParameterizedAssertion<>();
    }

    public static StatsAssertion<ParameterizedMixedAssertion,SpeedStats>
            withTolerance(final Ratio tolerance) {
        return new AssertPerformance<ParameterizedMixedAssertion,SpeedStats>(null,
                    new ArrayList<Assertion<SpeedStats>>())
                .withTolerance(tolerance);
    }
}
