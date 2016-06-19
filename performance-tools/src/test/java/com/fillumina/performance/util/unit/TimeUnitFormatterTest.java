package com.fillumina.performance.util.unit;

import java.util.Arrays;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati
 */
public class TimeUnitFormatterTest {
    private static final double SECOND = 1E9;
    private static final double MINUTE = 60.0 * SECOND;
    private static final double HOUR = 60.0 * MINUTE;

    @Test
    public void shouldSelectNanoseconds() {
        assertTimeUnit(TimeUnit.NANOSECONDS, 2, 3, 1, 8);
        assertTimeUnit(TimeUnit.NANOSECONDS, 2, 3, 1, 8, 102000);
        assertTimeUnit(TimeUnit.NANOSECONDS, 12, 7, 0, 800);
        assertTimeUnit(TimeUnit.NANOSECONDS, 123, 700, 134);
    }

    @Test
    public void shouldSelectMicroseconds() {
        assertTimeUnit(TimeUnit.MICROSECONDS, 2_300, 1_000, 212_000);
        assertTimeUnit(TimeUnit.MICROSECONDS, 12_300, 1_000, 212_000);
        assertTimeUnit(TimeUnit.MICROSECONDS, 123_300, 1_000, 1_212_000);
    }

    @Test
    public void shouldSelectMilliseconds() {
        assertTimeUnit(TimeUnit.MILLISECONDS, 1_000_000, 21_213_544);
        assertTimeUnit(TimeUnit.MILLISECONDS, 10_000_000, 212_213_544);
        assertTimeUnit(TimeUnit.MILLISECONDS, 100_000_000D, 121_213_544D);
    }

    @Test
    public void shouldSelectSeconds() {
        assertTimeUnit(TimeUnit.SECONDS, 1_000_000_000L, 354_121_213_456L);
        assertTimeUnit(TimeUnit.SECONDS, 10_000_000_000L, 354_121_213_789L);
    }

    @Test
    public void shouldSelectMinutes() {
        assertTimeUnit(TimeUnit.MINUTES, 3.6 * MINUTE, 1 * HOUR);
    }

    @Test
    public void shouldSelectHours() {
        assertTimeUnit(TimeUnit.HOURS, HOUR * 4, HOUR);
    }

    private void assertTimeUnit(final TimeUnit expected, double... values) {
        Unit result = TimeUnit.SECONDS.getFormatter().getUnit(values);
        assertEquals(" values: " + Arrays.toString(values),
                expected, result);
    }
}
