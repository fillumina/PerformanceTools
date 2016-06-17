package com.fillumina.performance.speed.stats;

import com.fillumina.performance.assertion.AssertParametrizedPerformance;
import com.fillumina.performance.assertion.AssertParametrizedSequencePerformance;
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

    public static AssertParametrizedSequencePerformance<Void, PerformanceStats>
            parametrizedSequence() {
        return new AssertParametrizedSequencePerformance<>();
    }

    public static AssertParametrizedPerformance<Void, PerformanceStats>
            parametrized() {
        return new AssertParametrizedPerformance<>();
    }

    public static StatsAssertion<PerformanceStats> withTolerance(
            final double tolerance) {
        return new AssertPerformance<>(new ArrayList<Assertion<PerformanceStats>>())
                .withPercentageTolerance(tolerance);
    }
}
