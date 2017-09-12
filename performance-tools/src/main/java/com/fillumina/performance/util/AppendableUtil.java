package com.fillumina.performance.util;

import java.io.IOException;
import java.util.Arrays;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AppendableUtil {

    public static <T> void append(Appendable appendable,
            String separator,
            T... array)
            throws IOException {
        append(appendable, separator, Arrays.asList(array));
    }

    public static <T> void append(Appendable appendable,
            String separator,
            Iterable<T> coll)
            throws IOException {
        boolean first = true;
        for (T t : coll) {
            if (first) {
                first = false;
            } else {
                appendable.append(separator);
            }
            appendable.append(t.toString());
        }
    }
}
