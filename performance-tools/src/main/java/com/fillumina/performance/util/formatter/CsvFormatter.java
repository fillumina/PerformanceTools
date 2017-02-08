package com.fillumina.performance.util.formatter;

import java.util.Objects;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class CsvFormatter {
    private static final String SEPARATOR = ", ";
    private boolean expectedSeparator = false;
    private final StringBuilder buf = new StringBuilder();

    /**
     * @param values are appended to each other with a separator
     *       <i>between</i> them ended with a line separator.
     */
    public CsvFormatter line(Object... values) {
        for (Object o : values) {
            append(o);
        }
        endl();
        return this;
    }

    /**
     * @param values are appended to each other with a separator at the
     *       <i>end</i>
     */
    public CsvFormatter append(Object... values) {
        appendSeparator();
        for (Object v : values) {
            buf.append(v.toString());
        }
        expectedSeparator = true;
        return this;
    }

    /**
     * @param values are appended to each other with a separator at the
     *       <i>end</i>
     */
    public CsvFormatter append(String... values) {
        appendSeparator();
        for (String v : values) {
            buf.append(v);
        }
        expectedSeparator = true;
        return this;
    }

    private void appendSeparator() {
        if (expectedSeparator) {
            buf.append(SEPARATOR);
            expectedSeparator = false;
        }
    }

    /** Adds a line separator. */
    public CsvFormatter endl() {
        buf.append(System.lineSeparator());
        expectedSeparator = false;
        return this;
    }

    @Override
    public String toString() {
        return buf.toString();
    }

    /**
     * @param values are appended to each other with a separator
     *       <i>between</i> them.
     */
    public static String toString(Object... fields) {
        StringBuilder buf = new StringBuilder();
        buf.append(fields[0]);
        for (int i=1, l=fields.length; i<l; i++) {
            buf.append(SEPARATOR).append(Objects.toString(fields[i], " "));
        }
        return buf.toString();
    }
}
