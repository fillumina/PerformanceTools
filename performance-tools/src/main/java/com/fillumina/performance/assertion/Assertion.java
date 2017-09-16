package com.fillumina.performance.assertion;

import com.fillumina.performance.util.BindingConsumer;
import com.fillumina.performance.util.StringGenerator;

/**
 * A {@link BindingConsumer} that checks if the statistics comply with the
 * requirements.
 * It implements {@link StringGenerator} so that requirements can be
 * printed out nicely. Note that assertions don't really need to implement
 * this interface, {@link BindingConsumer} is all they need because
 * they are not treated differently than any other consumer.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface Assertion
        extends BindingConsumer<Assertable>,
                StringGenerator<Assertable> {

    @Override
    default Class<Assertable> getAcceptedAssertableClass() {
        return Assertable.class;
    }

    default void check(Assertable assertable) throws AssertionError {
        accept(assertable);
    }

    default boolean satisfy(Assertable assertable) {
        try {
            check(assertable);
            return true;
        } catch (AssertionError ae) {
            return false;
        }
    }
}
