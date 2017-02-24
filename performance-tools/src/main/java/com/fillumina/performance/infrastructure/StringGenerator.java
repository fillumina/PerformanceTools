package com.fillumina.performance.infrastructure;

import com.fillumina.performance.assertion.Assertable;

/**
 * Returns a String representation of the given object.
 *
 * @param A assertable
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface StringGenerator<A extends Assertable> {

    /** @return a String representation for the given object. */
    String toString(PHolder<A> t);
}
