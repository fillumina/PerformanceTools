package com.fillumina.performance.util;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class PerformanceLog {
    private final static String CRLF = System.lineSeparator();

    private final StringBuilder buf = new StringBuilder();

    public void log(String msg) {
        buf.append(msg).append(CRLF);
    }

    @Override
    public String toString() {
        return buf.toString();
    }
}
