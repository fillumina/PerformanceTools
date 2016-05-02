package com.fillumina.performance.util;

/**
 *
 * @author Francesco Illuminati
 */
public class StringHelper {

    public static String emptyOnNull(final String str) {
        return str == null ? "" : str;
    }

    public static String concat(String separator, String... strings) {
        if (strings == null || strings.length == 0) {
            return "";
        }
        StringBuilder buf = new StringBuilder();
        for (String s : strings) {
            if (s != null && buf.length() != 0) {
                buf.append(separator);
            }
            buf.append(s);
        }
        return buf.toString();
    }
}
