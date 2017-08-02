package com.fillumina.performance.assertion;

import com.fillumina.performance.infrastructure.AssertableConsumer;
import com.fillumina.performance.infrastructure.AssertableStringGenerator;

/**
 * A {@link AssertableConsumer} that checks if the statistics comply with the
 * requirements.
 * It implements {@link AssertableStringGenerator} so that requirements can be
 * printed out nicely. Note that assertions don't really need to implement
 * this interface, {@link AssertableConsumer} is all they need because
 * they are not treated differently than any other consumer.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface Assertion
        extends AssertableConsumer<Assertable>,
                AssertableStringGenerator<Assertable> {

    void check(Assertable assertable) throws AssertionError;

    @Override
    default Class<Assertable> getAcceptedAssertableClass() {
        return Assertable.class;
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
