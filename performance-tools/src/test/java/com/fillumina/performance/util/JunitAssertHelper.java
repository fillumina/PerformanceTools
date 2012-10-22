package com.fillumina.performance.util;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class JunitAssertHelper {

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
            final float tolerancePercentage) {
        final double tolerance = tolerancePercentage / 100;
        if ((expected < result * (1 - tolerance)) ||
                (expected > result * (1 + tolerance))) {
            throw new AssertionError(message +
                    ": The given value " + result +
                    " is not equals to the expected " + expected +
                    " within the tolerance of " + tolerancePercentage + "%");
        }
    }

}
