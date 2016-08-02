package com.fillumina.performance.assertion;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface AssertParametrizedPerformance<C, A extends AssertableMultiStats> {

    AssertParametrizedPerformanceImpl<C, A> forAllTests(
            StatsAssertion<A> performanceAssertion);

    AssertParametrizedPerformanceImpl<C, A> forRegexpTest(String regexp,
            StatsAssertion<A> performanceAssertion);

    AssertParametrizedPerformanceImpl<C, A> forTest(String testName,
            StatsAssertion<A> performanceAssertion);

    C endTests();
}
