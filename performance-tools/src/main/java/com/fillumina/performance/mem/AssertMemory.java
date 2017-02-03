package com.fillumina.performance.mem;

import com.fillumina.performance.assertion.AssertParameterizedPerformanceImpl;
import com.fillumina.performance.assertion.AssertParameterizedSequencePerformanceImpl;
import com.fillumina.performance.assertion.AssertPerformance;
import com.fillumina.performance.assertion.Assertion;
import com.fillumina.performance.assertion.StatsAssertion;
import com.fillumina.performance.template.ParameterizedAssertion;
import com.fillumina.performance.util.ComposedName;
import java.util.ArrayList;
import java.util.Map;

/**
 * It's a factory for speed related assertions.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AssertMemory {

    public static AssertParameterizedSequencePerformanceImpl
                    <Assertion<Map<ComposedName, Map<ComposedName, MemStats>>>,
                        MemStats>
            parameterizedSequence() {
        return new AssertParameterizedSequencePerformanceImpl<>();
    }

    public static AssertParameterizedPerformanceImpl
                    <Assertion<Map<ComposedName, MemStats>>, MemStats>
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
