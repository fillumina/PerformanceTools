package com.fillumina.performance.util;

/**
 *
 * @author fra
 */
public class NullString {

    public static String empty(final String str) {
        return str == null ? "" : str;
    }
}
