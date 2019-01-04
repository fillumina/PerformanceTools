package com.fillumina.performance.util;

import java.time.Duration;
import java.util.Calendar;
import java.util.Date;

/**
 * Helps creating an interval expressed in nanoseconds.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class TimeSpan {
    private static final long MICROS = 1_000;
    private static final long MILLIS = 1_000_000;
    private static final long SECOND = 1_000_000_000;
    private static final long MINUTE = 60 * SECOND;
    private static final long HOUR = 60 * MINUTE;

    /**
     * Use with fluent interface:
     * <pre>
     * {@code
     * long u = TimeSpan.set().hour(2).min(12).sec(4).asMicros();
     * }
     * </pre>
     * @return
     */
    public static TimeSpan set() {
        return new TimeSpan();
    }

    public static TimeSpan from(Duration duration) {
        return new TimeSpan(duration);
    }

    public static TimeSpan from(Date date) {
        return new TimeSpan(date);
    }

    public static TimeSpan from(Calendar cal) {
        return new TimeSpan(cal);
    }

    private long ns;

    public TimeSpan() {
    }

    public TimeSpan(Duration duration) {
        this(duration.toNanos());
    }

    public TimeSpan(Date date) {
        this(date.getTime() * MILLIS);
    }

    public TimeSpan(Calendar cal) {
        this(cal.getTimeInMillis() * MILLIS);
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

    public long asMicros() {
        return ns / MICROS;
    }

    public long asMillis() {
        return ns / MILLIS;
    }

    public long asSeconds() {
        return ns / SECOND;
    }

    public long asMinutes() {
        return ns / MINUTE;
    }

    public long asHours() {
        return ns / HOUR;
    }

    @Override
    public String toString() {
        return "TimeSpan{" + "ns=" + ns + '}';
    }
}
