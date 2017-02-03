package com.fillumina.performance.assertion;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface AssertParameterizedPerformance
                        <C, A extends AssertableMultiStats> {

    StatsAssertion<AssertParameterizedPerformance<C,A>,A> forAllTests();

    StatsAssertion<AssertParameterizedPerformance<C,A>,A> forRegexpTest(
            String regexp);

    StatsAssertion<AssertParameterizedPerformance<C,A>,A> forTest(String testName);

    C endTests();
}
