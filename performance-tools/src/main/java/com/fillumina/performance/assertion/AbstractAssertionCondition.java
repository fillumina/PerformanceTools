package com.fillumina.performance.assertion;

import com.fillumina.performance.util.ReturningToCallerImpl;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class AbstractAssertionCondition<C, A extends AssertableMultiStats>
        extends ReturningToCallerImpl<C>
        implements Assertion<A> {

    public AbstractAssertionCondition(C caller) {
        super(caller);
    }
}
