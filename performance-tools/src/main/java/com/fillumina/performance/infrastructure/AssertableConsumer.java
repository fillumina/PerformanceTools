package com.fillumina.performance.infrastructure;

import com.fillumina.performance.assertion.Assertable;
import java.util.function.Consumer;

/**
 * Consumes statistics.
 *
 * @param A assertable
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface AssertableConsumer<A extends Assertable>
    extends Consumer<A> {

    Class<A> getAcceptedAssertableClass();

    /**
     * Sometimes a base class needs to be passed and it must be check
     * for compatibility.
     *
     * @param assertable
     */
    @SuppressWarnings("unchecked")
    default void consumeAssertable(Assertable assertable) {
        if (assertable != null) {
            Class<?> accepted = getAcceptedAssertableClass();
            if (accepted.isAssignableFrom(assertable.getClass())) {
                accept((A)assertable);
            }
        }
    }
}
