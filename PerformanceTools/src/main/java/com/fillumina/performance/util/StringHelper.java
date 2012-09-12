package com.fillumina.performance.util;

/**
 *
 * @author fra
 */
public class StringHelper {

    public static String emptyOnNull(final String str) {
        return str == null ? "" : str;
    }
}
