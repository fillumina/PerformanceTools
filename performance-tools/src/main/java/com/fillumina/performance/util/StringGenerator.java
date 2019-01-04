package com.fillumina.performance.util;

import java.io.IOException;

/**
 * Returns a String representation of the given object.
 *
 * @param T assertable
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface StringGenerator<T> {

    /** @return a String representation for the given object. */
    void appendTo(Appendable appendable, T obj)
            throws IOException;

    default void appendToCatchingException(
            Appendable appendable, T obj) {
        try {
            appendTo(appendable, obj);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    default String toString(T obj) {
        StringBuilder buf = new StringBuilder();
        appendToCatchingException(buf, obj);
        return buf.toString();
    }
}
