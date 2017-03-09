package com.fillumina.performance.assertion;

import com.fillumina.performance.infrastructure.PHolder;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface ParameterizedAssertion<C, A extends Assertable>
        extends Assertion<PHolder<A>> {

    StatsAssertion<ParameterizedAssertion<C,A>,A> forAllTests();

    StatsAssertion<ParameterizedAssertion<C,A>,A> forRegexpTest(
            String regexp);

    StatsAssertion<ParameterizedAssertion<C,A>,A> forTest(String testName);

    ParameterizedAssertion<C, A> addAssertion(Assertion<A> assertion);

    C endTests();
}
