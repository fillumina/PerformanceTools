package com.fillumina.performance.assertion;

import java.io.IOException;
import java.util.Collection;
import java.util.function.Consumer;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MultiAssertionFactory
        implements Assertion, MultiAssertion {

    private final Collection<Assertion> collection;

    public static MultiAssertion createFrom(Collection<Assertion> coll) {
        return new MultiAssertionFactory(coll);
    }

    public MultiAssertionFactory(Collection<Assertion> collection) {
        this.collection = collection;
    }

    @Override
    public void iterateAssertions(Assertable assertable,
            Consumer<Assertion> consumer) {
        for (Assertion a : collection) {
            if (a instanceof MultiAssertion) {
                ((MultiAssertion) a).iterateAssertions(assertable, consumer);
            } else {
                consumer.accept(a);
            }
        }
    }

    @Override
    public void appendTo(Appendable appendable, Assertable assertable)
            throws IOException {
        iterateAssertions(assertable, (assertion) -> {
            assertion.appendToCatchingException(appendable, assertable);
        });
    }

    @Override
    public void accept(Assertable assertable) {
        iterateAssertions(assertable, (assertion) -> {
            assertion.accept(assertable);
        });
    }
}
