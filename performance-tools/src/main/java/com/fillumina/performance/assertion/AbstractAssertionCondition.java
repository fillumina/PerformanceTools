package com.fillumina.performance.assertion;

import com.fillumina.performance.util.ReentrantFluidInterfaceImpl;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class AbstractAssertionCondition<C, A extends Assertable>
        extends ReentrantFluidInterfaceImpl<C>
        implements Assertion<A> {

    public AbstractAssertionCondition(C caller) {
        super(caller);
    }
}
