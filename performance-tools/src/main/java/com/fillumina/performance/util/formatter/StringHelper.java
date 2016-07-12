package com.fillumina.performance.util.formatter;

/**
 *
 * @author Francesco Illuminati
 */
public class StringHelper {

    public static String emptyOnNull(final String str) {
        return str == null ? "" : str;
    }

    public static String createName(String... strs) {
        StringBuilder buf = new StringBuilder();
        for (int i=0, len = strs.length; i<len; i++) {
            String s = strs[i];
            if (s != null) {
                buf.append(s);
                if (i != len -1) {
                    buf.append('_');
                }
            }
        }
        return buf.toString();
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
