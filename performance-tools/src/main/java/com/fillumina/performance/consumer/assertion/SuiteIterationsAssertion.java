package com.fillumina.performance.consumer.assertion;

/**
 * Asserts conditions about tests executed a specified number of iterations
 * (it checks the iterations passed along with the
 * {@link com.fillumina.performance.producer.LoopPerformances}).
 *
 * @author Francesco Illuminati
 */
public interface SuiteIterationsAssertion {

    /**
     * Allows to check the results obtained with a given number
     * of iterations. It can be used to check if a particular code improves
     * its performances during its execution.
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
