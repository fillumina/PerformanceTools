package com.fillumina.performance.speed.stats;

import com.fillumina.performance.assertion.AssertParametrizedPerformanceImpl;
import com.fillumina.performance.assertion.AssertParametrizedSequencePerformanceImpl;
import com.fillumina.performance.assertion.AssertPerformance;
import com.fillumina.performance.assertion.Assertion;
import com.fillumina.performance.assertion.StatsAssertion;
import java.util.ArrayList;

/**
 * It's a factory for speed related assertions.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AssertSpeed {

    public static AssertParametrizedSequencePerformanceImpl<Void, SpeedStats>
            parametrizedSequence() {
        return new AssertParametrizedSequencePerformanceImpl<>();
    }

    public static AssertParametrizedPerformanceImpl<Void, SpeedStats>
            parametrized() {
        return new AssertParametrizedPerformanceImpl<>();
    }

    public static StatsAssertion<SpeedStats> withTolerance(
            final double tolerance) {
        return new AssertPerformance<>(new ArrayList<Assertion<SpeedStats>>())
                .withPercentageTolerance(tolerance);
    }
}
