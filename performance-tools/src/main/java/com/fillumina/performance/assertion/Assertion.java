package com.fillumina.performance.assertion;

import com.fillumina.performance.util.StringGenerator;
import java.util.function.Consumer;

/**
 * A {@link Consumer} that checks if the statistics comply with the
 * requirements.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface Assertion
        extends Consumer<Assertable>,
                StringGenerator<Assertable> {

    /** It's a more meaningful name for {@link #accept(Assertable)}. */
    default void check(Assertable assertable) throws AssertionError {
        accept(assertable);
    }

    default boolean satisfy(Assertable assertable) {
        try {
            check(assertable);
            return true;
        } catch (AssertionError err) {
            return false;
        }
    }
}
