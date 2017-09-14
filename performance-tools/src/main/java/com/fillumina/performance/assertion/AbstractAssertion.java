package com.fillumina.performance.assertion;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class AbstractAssertion implements Assertion {

    /**
     * 'Check' is just a prettier verb to use with assertions than 'accept'.
     * @see #accept(com.fillumina.performance.assertion.Assertable)
     */
    @Override
    public void check(Assertable assertable) {
        accept(assertable);
    }
}
