package com.fillumina.performance.mem;

import com.fillumina.performance.assertion.AssertPerformance;
import com.fillumina.performance.assertion.Assertion;
import com.fillumina.performance.assertion.ParameterizedAssertion;
import com.fillumina.performance.assertion.ParameterizedSequenceAssertion;
import com.fillumina.performance.assertion.StatsAssertion;
import com.fillumina.performance.template.ParameterizedMixedAssertion;
import java.util.ArrayList;

/**
 * It's a factory for speed related assertions.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AssertMemory {

    public static ParameterizedSequenceAssertion
                    <Assertion<MemStats>,MemStats>
            parameterizedSequence() {
        return new ParameterizedSequenceAssertion<>();
    }

    public static ParameterizedAssertion
                    <Assertion<MemStats>, MemStats>
            parameterized() {
        return new ParameterizedAssertion<>();
    }

    public static StatsAssertion<ParameterizedMixedAssertion,MemStats> withTolerance(
            final double tolerance) {
        return new AssertPerformance<ParameterizedMixedAssertion,MemStats>(null,
                    new ArrayList<Assertion<MemStats>>())
                .withTolerance(tolerance);
    }
}
