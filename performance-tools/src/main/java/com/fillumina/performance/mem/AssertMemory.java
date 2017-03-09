package com.fillumina.performance.mem;

import com.fillumina.performance.assertion.AssertParameterized;
import com.fillumina.performance.assertion.AssertParameterizedSequence;
import com.fillumina.performance.assertion.AssertStats;
import com.fillumina.performance.assertion.Assertion;
import com.fillumina.performance.assertion.StatsAssertion;
import com.fillumina.performance.template.ParameterizedMixedAssertion;
import com.fillumina.performance.util.stats.Ratio;

/**
 * It's a factory for speed related assertions.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AssertMemory {

    public static AssertParameterizedSequence
                    <Assertion<MemStats>,MemStats>
            parameterizedSequence() {
        return new AssertParameterizedSequence<>();
    }

    public static AssertParameterized
                    <Assertion<MemStats>, MemStats>
            parameterized() {
        return new AssertParameterized<>();
    }

    public static StatsAssertion<ParameterizedMixedAssertion,MemStats> withTolerance(
            final Ratio tolerance) {
        return new AssertStats<ParameterizedMixedAssertion,MemStats>()
                .setTolerance(tolerance);
    }
}
