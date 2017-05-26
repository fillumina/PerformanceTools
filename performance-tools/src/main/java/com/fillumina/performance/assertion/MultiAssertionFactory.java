package com.fillumina.performance.assertion;

import java.io.IOException;
import java.util.Collection;
import java.util.function.Consumer;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MultiAssertionFactory<A extends Assertable>
        extends AbstractAssertion<A>
        implements MultiAssertion<A> {

    private final Collection<Assertion<A>> collection;

    public static <A extends Assertable> MultiAssertion<A> createFrom(
            Collection<Assertion<A>> coll) {
        return new MultiAssertionFactory<>(coll);
    }

    public MultiAssertionFactory(Collection<Assertion<A>> collection) {
        this.collection = collection;
    }

    @Override
    public void iterateAssertions(A assertable,
            Consumer<Assertion<A>> consumer) {
        for (Assertion<A> a : collection) {
            if (a instanceof MultiAssertion) {
                ((MultiAssertion<A>) a).iterateAssertions(assertable, consumer);
            } else {
                consumer.accept(a);
            }
        }
    }

    @Override
    public void appendTo(Appendable appendable, A assertable)
            throws IOException {
        iterateAssertions(assertable, (assertion) -> {
            assertion.appendToCatchingException(appendable, assertable);
        });
    }

    @Override
    public void consume(A assertable) {
        iterateAssertions(assertable, (assertion) -> {
            assertion.consume(assertable);
        });
    }
}
