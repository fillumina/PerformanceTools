package com.fillumina.performance.assertion;

import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.infrastructure.PerformanceHolder;
import com.fillumina.performance.infrastructure.StringGenerator;

/**
 * A {@link PerformanceConsumer} that checks if the statistics comply to the
 * requirements. Useful to test performance requirements.
 * It implements {@link StringGenerator} so that requirements can be
 * printed out nicely if needed but assertions don't really need that and
 * can only implements {@link PerformanceConsumer} if they choose to
 * (they are not used differently than any other consumers by executors).
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface Assertion<A extends Assertable>
        extends PerformanceConsumer<A>, StringGenerator<A> {

    /**
     * It checks the given performance against its assertions.
     *
     * @throws AssertionError if the statistics are not as required.
     */
    void check(PerformanceHolder<A> assertable);
}
