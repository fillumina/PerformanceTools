package com.fillumina.performance.util;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class NanosecondTimeBuilder {
    private static final long MICROS = 1_000;
    private static final long MILLIS = 1_000_000;
    private static final long SECOND = 1_000_000_000;
    private static final long MINUTE = 60 * SECOND;
    private static final long HOUR = 60 * MINUTE;

    private long ns;

    public NanosecondTimeBuilder hour(final long value) {
        ns += value * HOUR;
        return this;
    }

    public NanosecondTimeBuilder min(final long value) {
        this.ns += value * MINUTE;
        return this;
    }

    public NanosecondTimeBuilder sec(final long value) {
        this.ns += value * SECOND;
        return this;
    }

    public NanosecondTimeBuilder millis(final long value) {
        this.ns += value * MILLIS;
        return this;
    }

    public NanosecondTimeBuilder micros(final long value) {
        this.ns += value * MICROS;
        return this;
    }

    public NanosecondTimeBuilder nanos(final long value) {
        this.ns += value;
        return this;
    }

    public long getNanoseconds() {
        return ns;
    }
}
