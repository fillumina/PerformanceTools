package com.fillumina.performance.mem;

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
public class AssertMemory {

    public static AssertParametrizedSequencePerformanceImpl<Void, MemStats>
            parametrizedSequence() {
        return new AssertParametrizedSequencePerformanceImpl<>();
    }

    public static AssertParametrizedPerformanceImpl<Void, MemStats>
            parametrized() {
        return new AssertParametrizedPerformanceImpl<>();
    }

    public static StatsAssertion<MemStats> withTolerance(
            final double tolerance) {
        return new AssertPerformance<>(new ArrayList<Assertion<MemStats>>())
                .withPercentageTolerance(tolerance);
    }
}
