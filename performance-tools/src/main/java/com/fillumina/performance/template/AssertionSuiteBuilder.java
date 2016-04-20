package com.fillumina.performance.template;

import com.fillumina.performance.stats.assertion.AssertPerformanceForExecutionSuite;
import com.fillumina.performance.stats.assertion.SuiteExecutionAssertion;

/**
 *
 * @author Francesco Illuminati
 */
public class AssertionSuiteBuilder {

    private AssertPerformanceForExecutionSuite assertion;

    public SuiteExecutionAssertion withTolerance(final float percentage) {
        assertion = (AssertPerformanceForExecutionSuite)
                AssertPerformanceForExecutionSuite.withTolerance(percentage);
        return assertion;
    }

    AssertPerformanceForExecutionSuite getAssertPerformanceForExecutionSuite() {
        return assertion;
    }
}
