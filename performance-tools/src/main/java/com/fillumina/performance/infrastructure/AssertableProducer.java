package com.fillumina.performance.infrastructure;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.util.TName;


/**
 * A {@link AssertableProducer} produces named assertables.
 *
 * @param A statistics
 * @param T test
 *
 * @author Francesco Illuminati
 */
public interface AssertableProducer<A extends Assertable, T>
        extends TestContainer<T>, AssertableConsumerNotifier<A> {

    /** Gives a name to the test. */
    AssertableProducer<A,T> setName(TName name);

    /**
     * Executes the tests.
     *
     * @return the performances collected.
     */
    PHolder<A> execute();
}
