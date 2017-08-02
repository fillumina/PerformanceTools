package com.fillumina.performance.infrastructure;

import com.fillumina.performance.util.TName;


/**
 * A {@link AssertableProducer} produces named assertables.
 *
 * @param A statistics
 * @param T test
 *
 * @author Francesco Illuminati
 */
public interface AssertableProducer<T>
        extends TestContainer<T> {

    /** Set the test name. */
    AssertableProducer<T> setName(TName name);

    /**
     * Executes the tests.
     *
     * @return the performances collected.
     */
    MixedAssertableHolder execute();
}
