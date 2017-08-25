package com.fillumina.performance.infrastructure;

import com.fillumina.performance.assertion.Assertable;

/**
 * Consumes statistics.
 *
 * @param A assertable
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
@Deprecated // TODO use the Java 8 Consumer<Assertable>
public interface AssertableConsumer<A extends Assertable> {

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
                consume((A)assertable);
            }
        }
    }

    /** Consumes an {@link Assertable}. */
    void consume(A assertable);
}
