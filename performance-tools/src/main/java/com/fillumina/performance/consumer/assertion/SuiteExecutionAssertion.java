package com.fillumina.performance.consumer.assertion;

/**
 * Asserts conditions on a specific test of a suite
 * {@link com.fillumina.performance.producer.suite.ParametrizedPerformanceSuite}.
 *
 * @author fra
 */
public interface SuiteExecutionAssertion {

    /**
     * In a performance suite each test is executed against several parameters.
     * To assert conditions about those parameters you have first to select
     * the test by using this method.
     * <pre>
     *    assertion.forExecution("SEQUENTIAL READ")
     *           .assertTest("TreeMap").slowerThan("HashMap");
     * </pre>
     *
     * @param testName  the name of the test
     * @return          a {@link PerformanceAssertion} usable in a
     *                  fluid interface to define the conditions about the
     *                  specified tests.
     */
    PerformanceAssertion forExecution(final String testName);
}
