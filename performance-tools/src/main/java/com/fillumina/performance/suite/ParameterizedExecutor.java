package com.fillumina.performance.suite;

import com.fillumina.performance.speed.sample.SpeedSample;

/**
 *
 * @param P parameter
 *
 * @author Francesco Illuminati
 */
public interface ParameterizedExecutor<P> {

    /**
     * Executes the given test against the previously added parameters.
     * The default name for the test will be {@code null}.
     *
     * @return the same performance given to the consumer.
     */
    SpeedSample executeTest(
            final ParameterizedTestable<? extends P> test);

    /**
     * Executes the given named test against the previously added parameters.
     *
     * @return the same performance given to the consumer.
     */
    SpeedSample executeTest(final String name,
            final ParameterizedTestable<? extends P> test);

    /** Ignore the test. */
    SpeedSample ignoreTest(
            final ParameterizedTestable<? extends P> test);

    /** Ignore the test. */
    SpeedSample ignoreTest(final String name,
            final ParameterizedTestable<? extends P> test);

}
