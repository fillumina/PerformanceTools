package com.fillumina.performance.consumer.assertion;

/**
 *
 * @author fra
 */
public interface SuiteExecutionAssertion {

    /** Sets the test to assert. */
    PerformanceAssertion forExecution(final String testName);
}
