package com.fillumina.performance.util;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class StringHelper {

    public static String emptyOnNull(final String str) {
        return str == null ? "" : str;
    }
}
