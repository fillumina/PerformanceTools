package com.fillumina.performance.assertion;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class AbstractAssertion<A extends Assertable>
        implements Assertion<A> {

    /**
     * 'Check' is just a prettier verb to use with assertions than 'consume'.
     * @see #consume(com.fillumina.performance.util.TName, Assertable)
     */
    @Override
    public void check(A assertable) {
        consume(assertable);
    }

}
