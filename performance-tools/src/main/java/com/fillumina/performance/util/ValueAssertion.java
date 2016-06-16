package com.fillumina.performance.util;

/**
 *
 * @author Francesco Illuminati
 */
public class ValueAssertion {

    public static void isTrue(final boolean value, final String message) {
        if (!value) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void isNotNull(final Object value, final String message) {
        if (value == null) {
            throw new IllegalArgumentException(message + " cannot be null");
        }
    }
}
