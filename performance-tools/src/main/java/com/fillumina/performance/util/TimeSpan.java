package com.fillumina.performance.util;

import java.time.Duration;
import java.util.Date;

/**
 * Helps creating an interval expressed in nanoseconds.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
// TODO use this when a timeout is required (easy to setup, easy to understand)
public class TimeSpan {
    private static final long MICROS = 1_000;
    private static final long MILLIS = 1_000_000;
    private static final long SECOND = 1_000_000_000;
    private static final long MINUTE = 60 * SECOND;
    private static final long HOUR = 60 * MINUTE;

    public static TimeSpan set() {
        return new TimeSpan();
    }

    public static TimeSpan from(Duration duration) {
        return new TimeSpan(duration.toNanos());
    }

    public static TimeSpan from(Date date) {
        return new TimeSpan(date.getTime() * MILLIS);
    }

    private long ns;

    public TimeSpan() {
    }

    public TimeSpan(long ns) {
        this.ns = ns;
    }

    public TimeSpan hour(final long value) {
        ns += value * HOUR;
        return this;
    }

    public TimeSpan min(final long value) {
        this.ns += value * MINUTE;
        return this;
    }

    public TimeSpan sec(final long value) {
        this.ns += value * SECOND;
        return this;
    }

    public TimeSpan millis(final long value) {
        this.ns += value * MILLIS;
        return this;
    }

    public TimeSpan micros(final long value) {
        this.ns += value * MICROS;
        return this;
    }

    public TimeSpan nanos(final long value) {
        this.ns += value;
        return this;
    }

    public long asNanos() {
        return ns;
    }

    public long asMillis() {
        return ns / MILLIS;
    }
}
