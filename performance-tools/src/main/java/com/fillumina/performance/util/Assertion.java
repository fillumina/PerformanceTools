package com.fillumina.performance.util;

/**
 *
 * @author fra
 */
public class Assertion {

    public static void isTrue(final boolean value, final String message) {
        if (!value) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void isNotNull(final Object value, final String message) {
        if (value == null) {
            throw new IllegalArgumentException(message);
        }
    }
}
