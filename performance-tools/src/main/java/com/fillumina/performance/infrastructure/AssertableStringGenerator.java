package com.fillumina.performance.infrastructure;

import com.fillumina.performance.assertion.Assertable;
import java.io.IOException;

/**
 * Returns a String representation of the given object.
 *
 * @param Assertable assertable
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface AssertableStringGenerator<A extends Assertable> {

    /** @return a String representation for the given object. */
    void appendTo(Appendable appendable, A assertable)
            throws IOException;

    default void appendToCatchingException(
            Appendable appendable, A assertable) {
        try {
            appendTo(appendable, assertable);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    default String toString(A assertable) {
        StringBuilder buf = new StringBuilder();
        appendToCatchingException(buf, assertable);
        return buf.toString();
    }
}
