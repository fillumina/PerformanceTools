package com.fillumina.performance;

import java.util.Collection;

/**
 *
 * @author fra
 */
public class CsvHelper {

    public static String toCsvString(final Collection<Float> values) {
        StringBuilder buf = new StringBuilder();
        for (float d: values) {
            if (buf.length() != 0) {
                buf.append(", ");
            }
            buf.append(String.format("%.2f", d));
        }
        return buf.toString();
    }

}
