package com.fillumina.performance.util;

import com.fillumina.performance.util.stats.Ratio;

/**
 *
 * @author Francesco Illuminati
 */
public class ToleranceAssertion {


    public static void assertEquals(
            final double expected,
            final double result,
            final Ratio tolerancePercentage) {
        assertEquals("", expected, result, tolerancePercentage);
    }

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
    public static void assertEquals(
            final String message,
            final double expected,
            final double result,
            final Ratio tolerancePercentage) {
        final double tolerance = tolerancePercentage.getDecimal();
        if ((result < expected * (1 - tolerance)) ||
                (result > expected * (1 + tolerance))) {
            throw new AssertionError(message +
                    ": The given value " + result +
                    " is not equals to the expected " + expected +
                    " within the tolerance of " + tolerancePercentage.toString());
        }
    }

    public static void assertLess(
            final double lesser,
            final double bigger,
            final Ratio tolerancePercentage) {
        assertLess("", lesser, bigger, tolerancePercentage);
    }

    public static void assertLess(
            final String message,
            final double lesser,
            final double bigger,
            final Ratio tolerancePercentage) {
        final double tolerance = tolerancePercentage.getDecimal();
        if (lesser > bigger * (1 + tolerance)) {
            throw new AssertionError(message +
                    ": The given value " + bigger +
                    " is less than  " + lesser +
                    " within the tolerance of " + tolerancePercentage.toString());
        }
    }

    public static void assertGreater(
            final double bigger,
            final double lesser,
            final Ratio tolerancePercentage) {
        assertGreater("", bigger, lesser, tolerancePercentage);
    }

    public static void assertGreater(
            final String message,
            final double bigger,
            final double lesser,
            final Ratio tolerancePercentage) {
        final double tolerance = tolerancePercentage.getDecimal();
        if (bigger < lesser * (1 - tolerance)) {
            throw new AssertionError(message +
                    ": The given value " + bigger +
                    " is greater than  " + lesser +
                    " within the tolerance of " + tolerancePercentage.toString());
        }
    }
}
