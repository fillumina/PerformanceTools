package com.fillumina.performance.speed.stats;

import com.fillumina.performance.assertion.AssertParameterizedPerformanceImpl;
import com.fillumina.performance.assertion.AssertParameterizedSequencePerformanceImpl;
import com.fillumina.performance.assertion.AssertPerformance;
import com.fillumina.performance.assertion.Assertion;
import com.fillumina.performance.assertion.StatsAssertion;
import com.fillumina.performance.template.ParameterizedAssertion;
import java.util.ArrayList;

/**
 * It's a factory for speed related assertions.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AssertSpeed {

    public static AssertParameterizedSequencePerformanceImpl
                <Assertion<SpeedStats>, SpeedStats>
            parameterizedSequence() {
        return new AssertParameterizedSequencePerformanceImpl<>();
    }

    public static AssertParameterizedPerformanceImpl
                <Assertion<SpeedStats>, SpeedStats>
            parameterized() {
        return new AssertParameterizedPerformanceImpl<>();
    }

    public static StatsAssertion<ParameterizedAssertion,SpeedStats> withTolerance(
            final double tolerance) {
        return new AssertPerformance<ParameterizedAssertion,SpeedStats>(null,
                    new ArrayList<Assertion<SpeedStats>>())
                .withTolerance(tolerance);
    }
}
