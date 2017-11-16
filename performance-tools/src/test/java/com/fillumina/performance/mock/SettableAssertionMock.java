package com.fillumina.performance.mock;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.assertion.Assertion;
import java.io.IOException;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SettableAssertionMock implements Assertion {

    private final Consumer<Assertable> consumer;
    private final Function<Assertable,String> viewer;

    public SettableAssertionMock(Consumer<Assertable> consumer) {
        this(consumer, a -> a.toString());
    }

    public SettableAssertionMock(Consumer<Assertable> consumer,
            Function<Assertable, String> viewer) {
        this.consumer = consumer;
        this.viewer = viewer;
    }

    @Override
    public void accept(Assertable t) {
        consumer.accept(t);
    }

    @Override
    public void appendTo(Appendable appendable, Assertable assertable)
            throws IOException {
        appendable.append(viewer.apply(assertable));
    }

}
