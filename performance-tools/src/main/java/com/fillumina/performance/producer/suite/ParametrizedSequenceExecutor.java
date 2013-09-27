package com.fillumina.performance.producer.suite;

import com.fillumina.performance.producer.LoopPerformancesHolder;

/**
 *
 * @author fra
 */
public interface ParametrizedSequenceExecutor<P, S> {

    /**
     * Executes the given test against the previously added parameters and
     * sequence. The outer loop is the sequence so the consumers will be
     * called at each element for the sequence.
     *
     * @return the performances by parameter averaged for the elements of the
     *          sequence.
     */
    LoopPerformancesHolder executeTest(
            final ParametrizedSequenceRunnable<P, S> test);

    /**
     * Executes the given named test against the previously added parameters and
     * sequence. The outer loop is the sequence.
     *
     * @return the performances by parameter averaged for the elements of the
     *          sequence.
     */
    LoopPerformancesHolder executeTest(final String name,
            final ParametrizedSequenceRunnable<P, S> test);

    LoopPerformancesHolder ignoreTest(
            final ParametrizedSequenceRunnable<P, S> test);

    LoopPerformancesHolder ignoreTest(final String name,
            final ParametrizedSequenceRunnable<P, S> test);

}
