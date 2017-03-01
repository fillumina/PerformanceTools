package com.fillumina.performance.assertion;

import com.fillumina.performance.infrastructure.PHolder;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class AbstractAssertion<A extends Assertable>
        implements Assertion<A> {

    /**
     * 'Check' is just a prettier verb to use with assertions than 'consume'.
     */
    @Override
    public void check(PHolder<A> assertable) {
        consume(assertable);
    }

}
