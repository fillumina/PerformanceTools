package com.fillumina.performance.consumer.assertion;

/**
 * Asserts conditions on a specific test of a suite
 * {@link com.fillumina.performance.producer.suite.ParametrizedPerformanceSuite}.
 *
 * @author Francesco Illuminati
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
     * @return          a {@link PerformanceAssertion} usable in a <i>
     *                  <a href='http://en.wikipedia.org/wiki/Fluent_interface'>
     *                  fluent interface</a></i> to define the conditions
     *                  about the specified tests.
     */
    PerformanceAssertion forExecution(final String testName);

    /**
     * Same as {@link #forExecution(java.lang.String)} but using the default
     * {@code null} test name. With parametrized tests it's possible (and
     * even normal) to have only one parametrized test so that it's of no
     * use to name it).
     */
    PerformanceAssertion forDefaultExecution();
}
