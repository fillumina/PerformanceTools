package com.fillumina.performance.util;

/**
 *
 * @author fra
 */
public class StringHelper {

    public static String ifNotNull(final String str) {
        return str == null ? "" : str;
    }
}
