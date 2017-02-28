package com.fillumina.performance.assertion;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface ParameterizedAssertion<C, A extends Assertable> {

    StatsAssertion<ParameterizedAssertion<C,A>,A> forAllTests();

    StatsAssertion<ParameterizedAssertion<C,A>,A> forRegexpTest(
            String regexp);

    StatsAssertion<ParameterizedAssertion<C,A>,A> forTest(String testName);

    C endTests();
}
