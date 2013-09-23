package com.fillumina.performance.consumer.assertion;

/**
 * Asserts condition about tests executed a specified number of times.
 *
 * @author fra
 */
public interface SuiteIterationsAssertion {

    /**
     * In a performance progression
     * {@link com.fillumina.performance.producer.progression.
     * the same test can be executed with
     * different number of iterations and the results can be compared.
     * This method allows to check the results with a defined number
     * of iterations. It can be used to check if a particular code improves
     * its performances being executed.
     * <pre>
     *    assertion.forIterations(1_000)
     *           .assertTest("TreeMap").slowerThan("HashMap");
     * </pre>
     *
     * @param iterations  the number of iterations to check
     * @return          a {@link PerformanceAssertion} usable in a <i>
     *                  <a href='http://en.wikipedia.org/wiki/Fluent_interface'>
     *                  fluent interface</a></i> to define the conditions
     *                  about the specified tests.
     */
    PerformanceAssertion forIterations(final long iterations);
}
