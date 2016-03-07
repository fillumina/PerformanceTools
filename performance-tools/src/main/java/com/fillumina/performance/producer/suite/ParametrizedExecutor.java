package com.fillumina.performance.producer.suite;

import com.fillumina.performance.producer.LoopPerformancesHolder;

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
    LoopPerformancesHolder executeTest(
            final ParametrizedTestable<? extends P> test);

    /**
     * Executes the given named test against the previously added parameters.
     *
     * @return the same performance given to the consumer.
     */
    @SuppressWarnings(value = "unchecked")
    LoopPerformancesHolder executeTest(final String name,
            final ParametrizedTestable<? extends P> test);

    /** Ignore the test. */
    LoopPerformancesHolder ignoreTest(
            final ParametrizedTestable<? extends P> test);

    /** Ignore the test. */
    LoopPerformancesHolder ignoreTest(final String name,
            final ParametrizedTestable<? extends P> test);

}
