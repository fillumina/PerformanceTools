package com.fillumina.performance.assertion;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface AssertParameterizedPerformance<C, A extends AssertableMultiStats> {

    AssertParameterizedPerformanceImpl<C, A> forAllTests(
            StatsAssertion<A> performanceAssertion);

    AssertParameterizedPerformanceImpl<C, A> forRegexpTest(String regexp,
            StatsAssertion<A> performanceAssertion);

    AssertParameterizedPerformanceImpl<C, A> forTest(String testName,
            StatsAssertion<A> performanceAssertion);

    C endTests();
}
