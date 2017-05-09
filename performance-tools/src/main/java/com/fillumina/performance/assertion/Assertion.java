package com.fillumina.performance.assertion;

import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.infrastructure.StringGenerator;

/**
 * A {@link PerformanceConsumer} that checks if the statistics comply with the
 * requirements.
 * It implements {@link StringGenerator} so that requirements can be
 * printed out nicely. Note that assertions don't really need to implement
 * this interface, {@link PerformanceConsumer} is all they need because
 * they are not treated differently than any other consumer.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface Assertion<A extends Assertable>
        extends PerformanceConsumer<A>, StringGenerator<A> {

    /**
     * It checks the given statistics against its assertions.
     *
     * @throws AssertionError if the statistics are not as required.
     */
    void check(A assertable) throws AssertionError;
}
