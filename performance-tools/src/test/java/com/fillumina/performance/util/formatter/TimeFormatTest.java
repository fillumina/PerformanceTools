package com.fillumina.performance.util.formatter;

import com.fillumina.performance.util.TimeSpan;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class TimeFormatTest {
    private static final int HOUR = 3600;
    private static final int MINUTE = 60;

    @Test
    public void shouldFormat0Full() {
        assertEquals(" 0:00:00.000000000",
                TimeFormat.WATCH.nanos(0));
    }

    @Test
    public void shouldFormat1SecondFull() {
        assertEquals(" 0:00:01.000000000",
                TimeFormat.WATCH.nanos(1_000_000_000));
    }

    @Test
    public void shouldFormat1NanosecondFull() {
        assertEquals(" 0:00:00.000000001",
                TimeFormat.WATCH.nanos(1));
    }

    @Test
    public void shouldFormat12SecondsFull() {
        assertEquals(" 0:00:12.000000000",
                TimeFormat.WATCH.nanos(12_000_000_000L));
    }

    @Test
    public void shouldFormat35MinutesFull() {
        assertEquals(" 0:35:00.000000000",
                TimeFormat.WATCH.nanos(35 * 60 * 1_000_000_000L));
    }

    @Test
    public void shouldFormat7HoursFull() {
        assertEquals(" 7:00:00.000000000",
                TimeFormat.WATCH.nanos(7 * 60 * 60 * 1_000_000_000L));
    }

    @Test
    public void shouldFormat125HoursFull() {
        assertEquals(" 125:00:00.000000000",
                TimeFormat.WATCH.nanos(125 * 60 * 60 * 1_000_000_000L));
    }

    @Test
    public void shouldFormat125HoursNegativeFull() {
        assertEquals("-125:00:00.000000000",
                TimeFormat.WATCH.nanos(-125 * 60 * 60 * 1_000_000_000L));
    }

    @Test
    public void shouldFormat0Short() {
        assertEquals(" 0:00:00", TimeFormat.WATCH.second(0));
    }

    @Test
    public void shouldFormat1SecondShort() {
        assertEquals(" 0:00:01",
                TimeFormat.WATCH.second(1));
    }

    @Test
    public void shouldFormat12SecondsShort() {
        assertEquals(" 0:00:12",
                TimeFormat.WATCH.second(12));
    }

    @Test
    public void shouldFormat35MinutesShort() {
        assertEquals(" 0:35:00",
                TimeFormat.WATCH.second(35 * MINUTE));
    }

    @Test
    public void shouldFormat7HoursShort() {
        assertEquals(" 7:00:00",
                TimeFormat.WATCH.second(7 * HOUR));
    }

    @Test
    public void shouldFormat125HoursShort() {
        assertEquals(" 125:00:00",
                TimeFormat.WATCH.second(125 * HOUR));
    }

    @Test
    public void shouldFormat125HoursNegativeShort() {
        assertEquals("-125:00:00",
                TimeFormat.WATCH.second(-125 * HOUR));
    }

    @Test
    public void shouldFormat1MicrosecondsMedium() {
        assertEquals(" 0:00:00.001",
                TimeFormat.WATCH.millis(1));
    }

    @Test
    public void shouldFormat1MillisecondsLong() {
        assertEquals(" 0:00:00.000001",
                TimeFormat.WATCH.micros(1));
    }

    @Test
    public void shouldFormatTextSeconds() {
        assertEquals(" 12h 24m 32s",
                TimeFormat.TEXT.second(new TimeSpan()
                    .hour(12)
                    .min(24)
                    .sec(32)
                    .millis(456)
                    .asSeconds()));
    }

    @Test
    public void shouldFormatTextMillis() {
        assertEquals(" 12h 24m 32.456s",
                TimeFormat.TEXT.millis(new TimeSpan()
                    .hour(12)
                    .min(24)
                    .sec(32)
                    .millis(456)
                    .asMillis()));
    }

    @Test
    public void shouldFormatTextMicros() {
        assertEquals(" 12h 24m 32.456789s",
                TimeFormat.TEXT.micros(new TimeSpan()
                    .hour(12)
                    .min(24)
                    .sec(32)
                    .millis(456)
                    .micros(789)
                    .asMicros()));
    }

    @Test
    public void shouldFormatTextNanos() {
        assertEquals(" 12h 24m 32.456789321s",
                TimeFormat.TEXT.nanos(new TimeSpan()
                    .hour(12)
                    .min(24)
                    .sec(32)
                    .millis(456)
                    .micros(789)
                    .nanos(321)
                    .asNanos()));
    }

    @Test
    public void shouldFormatTextRemovingHour() {
        assertEquals(" 24m 32.456789321s",
                TimeFormat.TEXT.nanos(new TimeSpan()
                    .min(24)
                    .sec(32)
                    .millis(456)
                    .micros(789)
                    .nanos(321)
                    .asNanos()));
    }

    @Test
    public void shouldFormatTextNanosRemovingMinutes() {
        assertEquals(" 12h 32.456789321s",
                TimeFormat.TEXT.nanos(new TimeSpan()
                    .hour(12)
                    .sec(32)
                    .millis(456)
                    .micros(789)
                    .nanos(321)
                    .asNanos()));
    }

    @Test
    public void shouldFormatTextNanosUsingMillis() {
        assertEquals(" 12h .456000000s",
                TimeFormat.TEXT.nanos(new TimeSpan()
                    .hour(12)
                    .millis(456)
                    .asNanos()));
    }

    @Test
    public void shouldFormatTextNanosNotZeroPad() {
        assertEquals(" 1h 2m 3s",
                TimeFormat.TEXT.nanos(new TimeSpan()
                    .hour(1)
                    .min(2)
                    .sec(3)
                    .asNanos()));
    }

    @Test
    public void shouldPadFractionalNanosecondsInText() {
        assertEquals(" .000000001s", TimeFormat.TEXT.nanos(1));
    }

    @Test
    public void shouldFormatTextMillisWith0Seconds() {
        assertEquals(" 1m 5.123s",
                TimeFormat.TEXT.millis(65_123));

    }
}
