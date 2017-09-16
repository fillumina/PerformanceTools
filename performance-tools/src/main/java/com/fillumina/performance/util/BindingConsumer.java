package com.fillumina.performance.util;

import java.util.function.Consumer;

/**
 * Consumes statistics.
 *
 * @param T assertable
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface BindingConsumer<T>
    extends Consumer<T> {

    Class<?> getAcceptedAssertableClass();

    /**
     * Sometimes a base class needs to be passed and it must be check
     * for compatibility.
     *
     * @param assertable
     */
    @SuppressWarnings("unchecked")
    default void consumeAssertable(Object assertable) {
        if (assertable != null) {
            Class<?> accepted = getAcceptedAssertableClass();
            if (accepted.isAssignableFrom(assertable.getClass())) {
                accept((T)assertable);
            }
        }
    }
}
