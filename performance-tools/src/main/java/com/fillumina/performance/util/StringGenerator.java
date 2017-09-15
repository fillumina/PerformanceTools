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
    void appendTo(Appendable appendable, T assertable)
            throws IOException;

    default void appendToCatchingException(
            Appendable appendable, T assertable) {
        try {
            appendTo(appendable, assertable);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    default String toString(T assertable) {
        StringBuilder buf = new StringBuilder();
        appendToCatchingException(buf, assertable);
        return buf.toString();
    }
}
