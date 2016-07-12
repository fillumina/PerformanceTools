package com.fillumina.performance.util.formatter;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class CsvFormatter {
    private static final String SEPARATOR = ", ";
    private final StringBuilder buf = new StringBuilder();

    public CsvFormatter line(Object... values) {
        for (Object o : values) {
            append(o);
        }
        endl();
        return this;
    }

    public CsvFormatter append(Object... values) {
        if (buf.length() != 0) {
            buf.append(SEPARATOR);
        }
        for (Object v : values) {
            buf.append(v.toString());
        }
        return this;
    }

    public CsvFormatter append(String... values) {
        if (buf.length() != 0) {
            buf.append(SEPARATOR);
        }
        for (String v : values) {
            buf.append(v);
        }
        return this;
    }

    public CsvFormatter endl() {
        buf.append(System.lineSeparator());
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
