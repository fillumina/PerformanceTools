package com.fillumina.performance.infrastructure;

import com.fillumina.performance.util.ComposedName;

/**
 * Returns a String representation of the given object.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface StringGenerator<T> {

    /** @return a String representation for the given object. */
    String toString(T t);

    /** @return a String representation for the given named object. */
    String toString(ComposedName name, T t);
}
