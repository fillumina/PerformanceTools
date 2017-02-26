package com.fillumina.performance.assertion;

import com.fillumina.performance.util.stats.Ratio;

/**
 * {@link Assertion} builder working directly on {@link Assertable}.
 * It builds different types of assertions.
 *
 * @author Francesco Illuminati
 */
public interface StatsAssertion<C, A extends Assertable>
        extends Assertion<A> {
    Ratio DEFAULT_TOLERANCE = Ratio.percentage(5);
    Ratio SAFE_TOLERANCE = Ratio.percentage(7);
    Ratio SUPER_SAFE_TOLERANCE = Ratio.percentage(10);

    /** Asserts the percentage value against the slower test. */
    AssertPercentage<C, A> assertPercentage(final String testName);

    /** Asserts the relative order of the given test. */
    AssertOrder<C,A> assertOrder(final String testName);

    /** Asserts the mean value of the test. */
    AssertValue<C, A> assertValue(final String testName);

    /** Set the accepted tolerance. */
    StatsAssertion<C, A> withTolerance(final Ratio tolerance);
}
