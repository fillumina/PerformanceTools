package com.fillumina.performance.infrastructure;

import com.fillumina.performance.util.ComposedName;

/**
 * Return a String representation of the given performances.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface StringFormatter<T> {

    /** @return a String representation for the given object. */
    String toString(T t);

    /** @return a String representation for the given named object. */
    String toString(ComposedName name, T t);
}
