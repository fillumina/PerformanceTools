package com.fillumina.performance.assertion;

/**
 *
 * @author Francesco Illuminati
 */
public interface StatsAssertion<C, A extends AssertableMultiStats>
        extends Assertion<A> {
    double DEFAULT_TOLERANCE = 5;
    double SAFE_TOLERANCE = 7;
    double SUPER_SAFE_TOLERANCE = 10;

    /** Asserts the percentage ratio against the slower test. */
    AssertPercentage<C, A> assertPercentage(final String testName);

    /** Asserts the relative order of the given test. */
    AssertOrder<C,A> assertOrder(final String testName);

    /** Asserts the mean value of the test. */
    AssertValue<C, A> assertValue(final String testName);

    /**
     * Set the accepted tolerance percentage. i.e. 5 means 5%.
     * Choose values between 5 to 10 for normal tests and 1 or 2 if you
     * need a very precise measurement.
     */
    StatsAssertion<C, A> withTolerance(final double percentage);
}
