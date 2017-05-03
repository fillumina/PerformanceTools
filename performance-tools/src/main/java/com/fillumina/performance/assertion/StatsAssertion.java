package com.fillumina.performance.assertion;

import com.fillumina.performance.infrastructure.TN;
import com.fillumina.performance.util.TName;
import com.fillumina.performance.util.stats.Ratio;

/**
 * {@link Assertion} builder working directly on {@link Assertable}.
 * It builds different types of assertions.
 *
 * @author Francesco Illuminati
 */
public interface StatsAssertion<C, A extends Assertable>
        extends Assertion<A> {

    // TODO use those in tests (and move to test pkg)
    Ratio DEFAULT_TOLERANCE = Ratio.percentage(5);
    Ratio SAFE_TOLERANCE = Ratio.percentage(7);
    Ratio SUPER_SAFE_TOLERANCE = Ratio.percentage(10);

    /** Asserts the percentage decimal against the slower test. */
    default PercentageConditionBuilder<C, A> assertPercentage(String testName) {
        return assertPercentage(TN.n(testName));
    }
    PercentageConditionBuilder<C, A> assertPercentage(final TName testName);

    /** Asserts the relative order of the given test. */
    default OrderConditionBuilder<C, A> assertOrder(String testName) {
        return assertOrder(TN.n(testName));
    }
    OrderConditionBuilder<C,A> assertOrder(final TName testName);

    /** Asserts the mean decimal of the test. */
    default ValueConditionBuilder<C, A> assertValue(String testName) {
        return assertValue(TN.n(testName));
    }
    ValueConditionBuilder<C, A> assertValue(final TName testName);

    /** Adds an assertion. */
    AssertStats<C,A> addAssertion(Assertion<A> assertion);

    /** Set the accepted tolerance. */
    StatsAssertion<C, A> setTolerance(final Ratio tolerance);
}
