package com.fillumina.performance.mem;

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
public class AssertMemory {

    public static AssertParametrizedSequencePerformance<Void, MemStats>
            parametrizedSequence() {
        return new AssertParametrizedSequencePerformance<>();
    }

    public static AssertParametrizedPerformance<Void, MemStats>
            parametrized() {
        return new AssertParametrizedPerformance<>();
    }

    public static StatsAssertion<MemStats> withTolerance(
            final double tolerance) {
        return new AssertPerformance<>(new ArrayList<Assertion<MemStats>>())
                .withPercentageTolerance(tolerance);
    }
}
