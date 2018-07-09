package com.fillumina.performance.util;

import com.fillumina.performance.util.stats.Ratio;

/**
 *
 * @author Francesco Illuminati
 */
public class AssertHelper {

    /**
     * Assert that the given result value is equals to the expected
     * one within the tolerance.
     *
     * @param message       is shown in case of error
     * @param expected      the expected value
     * @param result        the result
     * @param tolerancePercentage   the tolerance expressed as a percentage,
     *              i.e. 5 means a percentage of 5% or 0.05.
     */
    public static void assertEqualsWithinPercentage(
            final String message,
            final double expected,
            final double result,
            final Ratio tolerancePercentage) {
        final double tolerance = tolerancePercentage.getDecimal();
        if ((expected < result * (1 - tolerance)) ||
                (expected > result * (1 + tolerance))) {
            throw new AssertionError(message +
                    ": The given value " + result +
                    " is not equals to the expected " + expected +
                    " within the tolerance of " + tolerancePercentage.toString());
        }
    }

    public static void assertEqualsWithinPercentage(
            final double expected,
            final double result,
            final Ratio tolerancePercentage) {
        assertEqualsWithinPercentage("", expected, result, tolerancePercentage);
    }

}
