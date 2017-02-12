package com.fillumina.performance.mem;

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
public class AssertMemory {

    public static AssertParameterizedSequencePerformanceImpl
                    <Assertion<MemStats>,MemStats>
            parameterizedSequence() {
        return new AssertParameterizedSequencePerformanceImpl<>();
    }

    public static AssertParameterizedPerformanceImpl
                    <Assertion<MemStats>, MemStats>
            parameterized() {
        return new AssertParameterizedPerformanceImpl<>();
    }

    public static StatsAssertion<ParameterizedAssertion,MemStats> withTolerance(
            final double tolerance) {
        return new AssertPerformance<ParameterizedAssertion,MemStats>(null,
                    new ArrayList<Assertion<MemStats>>())
                .withTolerance(tolerance);
    }
}
