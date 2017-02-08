package com.fillumina.performance.util.formatter;

/**
 *
 * @author Francesco Illuminati
 */
public class StringHelper {

    public static String createName(String... strs) {
        return concat("_", strs);
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
