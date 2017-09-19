package com.fillumina.performance.assertion;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.function.Consumer;

/**
 * Wraps a tree of {@link Assertion}s that can be visited with
 * both {@link #iterator()} and {@link #forEach(java.util.function.Consumer) }.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
@Deprecated
public class AddableMultiAssertion implements MultiAssertion {

    private final Collection<Assertion> collection;

    public AddableMultiAssertion() {
        this(new ArrayList<>());
    }

    public AddableMultiAssertion(Collection<Assertion> collection) {
        this.collection = collection;
    }

    public void addAssertion(Assertion assertion) {
        collection.add(assertion);
    }

    @Override
    public Iterator<Assertion> iterator() {
        return new Iterator<Assertion>() {
            private Deque<Iterator> stack = new ArrayDeque<>();
            private Iterator<Assertion> it = collection.iterator();

            @Override
            public boolean hasNext() {
                while (!it.hasNext() && !stack.isEmpty()) {
                    it = stack.pop();
                }
                return it.hasNext();
            }

            @Override
            public Assertion next() {
                Assertion a = it.next();
                while (a instanceof MultiAssertion) {
                    stack.push(it);
                    it = ((MultiAssertion) a).iterator();
                    a = it.next();
                }
                return a;
            }
        };
    }

    @Override
    public void accept(Assertable assertable) {
        forEach(a -> a.accept(assertable) );
    }

    @Override
    public void forEach(Consumer<? super Assertion> consumer) {
        for (Assertion a : collection) {
            if (a instanceof MultiAssertion) {
                ((MultiAssertion) a).forEach(consumer);
            } else {
                consumer.accept(a);
            }
        }
    }

    @Override
    public void appendTo(Appendable appendable, Assertable assertable)
            throws IOException {
        forEach(a -> a.appendToCatchingException(appendable, assertable));
    }
}
