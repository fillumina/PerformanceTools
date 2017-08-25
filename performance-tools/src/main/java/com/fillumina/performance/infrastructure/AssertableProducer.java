package com.fillumina.performance.infrastructure;

import com.fillumina.performance.util.tname.TName;


/**
 * A {@link AssertableProducer} produces assertables.
 *
 * @param A statistics
 * @param T test
 *
 * @author Francesco Illuminati
 */
public interface AssertableProducer<T> {

    /** Set the test name. */
    AssertableProducer<T> setName(TName name);

    /**
     * Executes the tests.
     *
     * @return the performances collected.
     */
    MixedAssertableHolder execute();
}
