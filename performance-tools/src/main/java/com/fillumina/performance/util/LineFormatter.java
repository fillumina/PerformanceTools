package com.fillumina.performance.util;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class LineFormatter {

    private final StringBuilder buf;

    public LineFormatter() {
        this.buf = new StringBuilder();
    }

    public LineFormatter(int initialSize) {
        this.buf = new StringBuilder(initialSize);
    }

    public LineFormatter append(Object s) {
        buf.append(s);
        return this;
    }

    public LineFormatter line(String... str) {
        for (String s : str) {
            buf.append(s);
        }
        buf.append(System.lineSeparator());
        return this;
    }
}
