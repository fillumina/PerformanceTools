package com.fillumina.performance.suite;

import com.fillumina.performance.sample.PerformanceSample;

/**
 *
 * @author Francesco Illuminati
 */
public interface ParametrizedExecutor<P> {

    /**
     * Executes the given test against the previously added parameters.
     * The default name for the test will be {@code null}.
     *
     * @return the same performance given to the consumer.
     */
    PerformanceSample executeTest(
            final ParametrizedTestable<? extends P> test);

    /**
     * Executes the given named test against the previously added parameters.
     *
     * @return the same performance given to the consumer.
     */
    @SuppressWarnings(value = "unchecked")
    PerformanceSample executeTest(final String name,
            final ParametrizedTestable<? extends P> test);

    /** Ignore the test. */
    PerformanceSample ignoreTest(
            final ParametrizedTestable<? extends P> test);

    /** Ignore the test. */
    PerformanceSample ignoreTest(final String name,
            final ParametrizedTestable<? extends P> test);

}
