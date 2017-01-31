package com.fillumina.performance.speed.stats;

import com.fillumina.performance.assertion.AssertParameterizedPerformanceImpl;
import com.fillumina.performance.assertion.AssertParameterizedSequencePerformanceImpl;
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

    public static AssertParameterizedSequencePerformanceImpl<Void, SpeedStats>
            parameterizedSequence() {
        return new AssertParameterizedSequencePerformanceImpl<>();
    }

    public static AssertParameterizedPerformanceImpl<Void, SpeedStats>
            parameterized() {
        return new AssertParameterizedPerformanceImpl<>();
    }

    public static StatsAssertion<SpeedStats> withTolerance(
            final double tolerance) {
        return new AssertPerformance<>(new ArrayList<Assertion<SpeedStats>>())
                .withPercentageTolerance(tolerance);
    }
}
