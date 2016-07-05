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
        assertTimeUnit(IntervalUnit.NANOSECONDS, 2, 3, 1, 8);
        assertTimeUnit(IntervalUnit.NANOSECONDS, 2, 3, 1, 8, 102000);
        assertTimeUnit(IntervalUnit.NANOSECONDS, 12, 7, 0, 800);
        assertTimeUnit(IntervalUnit.NANOSECONDS, 123, 700, 134);
    }

    @Test
    public void shouldSelectMicroseconds() {
        assertTimeUnit(IntervalUnit.MICROSECONDS, 2_300, 1_000, 212_000);
        assertTimeUnit(IntervalUnit.MICROSECONDS, 12_300, 1_000, 212_000);
        assertTimeUnit(IntervalUnit.MICROSECONDS, 123_300, 1_000, 1_212_000);
    }

    @Test
    public void shouldSelectMilliseconds() {
        assertTimeUnit(IntervalUnit.MILLISECONDS, 1_000_000, 21_213_544);
        assertTimeUnit(IntervalUnit.MILLISECONDS, 10_000_000, 212_213_544);
        assertTimeUnit(IntervalUnit.MILLISECONDS, 100_000_000D, 121_213_544D);
    }

    @Test
    public void shouldSelectSeconds() {
        assertTimeUnit(IntervalUnit.SECONDS, 1_000_000_000L, 354_121_213_456L);
        assertTimeUnit(IntervalUnit.SECONDS, 10_000_000_000L, 354_121_213_789L);
    }

    @Test
    public void shouldSelectMinutes() {
        assertTimeUnit(IntervalUnit.MINUTES, 3.6 * MINUTE, 1 * HOUR);
    }

    @Test
    public void shouldSelectHours() {
        assertTimeUnit(IntervalUnit.HOURS, HOUR * 4, HOUR);
    }

    private void assertTimeUnit(final IntervalUnit expected, double... values) {
        Unit result = IntervalUnit.SECONDS.getFormatter().getMinUnit(values);
        assertEquals(" values: " + Arrays.toString(values),
                expected, result);
    }
}
