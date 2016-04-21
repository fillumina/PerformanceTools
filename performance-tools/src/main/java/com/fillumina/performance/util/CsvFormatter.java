package com.fillumina.performance.util;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class CsvFormatter {
    private static final String SEPARATOR = ", ";
    private final StringBuilder buf = new StringBuilder();

    public CsvFormatter append(Object value) {
        return append(value.toString());
    }

    public CsvFormatter append(String value) {
        if (buf.length() != 0) {
            buf.append(SEPARATOR);
        }
        buf.append(value);
        return this;
    }

    public CsvFormatter endl() {
        buf.append('\n');
        return this;
    }

    @Override
    public String toString() {
        return buf.toString();
    }

    public static String toCsv(String... fields) {
        StringBuilder buf = new StringBuilder();
        buf.append(fields[0]);
        for (int i=1, l=fields.length; i<l; i++) {
            buf.append(SEPARATOR).append(fields[i]);
        }
        return buf.toString();
    }
}
