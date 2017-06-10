package com.fillumina.performance.infrastructure;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.util.TName;


/**
 * A {@link PerformanceProducer} produces named assertables.
 *
 * @param A statistics
 * @param T test
 *
 * @author Francesco Illuminati
 */
public interface PerformanceProducer<A extends Assertable, T>
        extends TestContainer<T>, PerformanceConsumerNotifier<A> {

    /** Gives a name to the test. */
    PerformanceProducer<A,T> setName(TName name);

    /**
     * Executes the tests.
     *
     * @return the performances collected.
     */
    PHolder<A> execute();
}
