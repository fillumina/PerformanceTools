package com.fillumina.performance.assertion;

import java.io.IOException;
import java.util.function.Consumer;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MultiAssertionNavigator implements MultiAssertion {

    private final Iterable<Assertion> iterable;

    public MultiAssertionNavigator(Iterable<Assertion> iterable) {
        this.iterable = iterable;
    }

    @Override
    public void forEach(Assertable assertable, Consumer<Assertion> consumer) {
        for (Assertion a : iterable) {
            if (a instanceof MultiAssertion) {
                ((MultiAssertion)a).forEach(assertable, consumer);
            } else {
                consumer.accept(a);
            }
        }
    }

    @Override
    public void accept(Assertable assertable) {
        forEach(assertable, a -> a.accept(assertable));
    }

    @Override
    public void appendTo(Appendable appendable, Assertable assertable)
            throws IOException {
        try {
            forEach(assertable, a -> {
                try {
                    a.appendTo(appendable, assertable);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });
        } catch (RuntimeException ex) {
            Throwable t = ex.getCause();
            if (t instanceof IOException) {
                throw (IOException) t;
            }
        }
    }

}
