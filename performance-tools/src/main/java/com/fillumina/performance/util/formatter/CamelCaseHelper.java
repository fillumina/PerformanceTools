package com.fillumina.performance.util.formatter;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class CamelCaseHelper {

    /**
     * Converts a JAVA camel case name to a phrase ignoring the first
     * world. i.e.: "shouldPrintOut" -&gt; "print out",
     * "getFirstElement" -&gt; "first element".
     */
    public static String convertToName(final String camelCase) {
        StringBuilder buf = new StringBuilder();
        boolean ignore = true;
        for (char c : camelCase.toCharArray()) {
            if (Character.isUpperCase(c)) {
                if (ignore) {
                    ignore = false;
                } else {
                    buf.append(' ');
                }
                c = Character.toLowerCase(c);
            }
            if (!ignore) {
                buf.append(c);
            }
        }
        return buf.toString();
    }
}
