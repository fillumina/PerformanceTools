package com.fillumina.performance.assertion;

import com.fillumina.performance.util.StringGenerator;
import java.util.function.Consumer;

/**
 * A {@link Consumer} that checks if the statistics comply with the
 * requirements.
 * It implements {@link StringGenerator} so that requirements can be
 * printed out nicely. Note that assertions don't really need to implement
 * this interface, {@link Consumer} is all they need because
 * they are not treated differently than any other consumer.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface Assertion
        extends Consumer<Assertable>,
                StringGenerator<Assertable> {

    /** It's a more meaningful alias for {@link #accept(Assertable)}. */
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
