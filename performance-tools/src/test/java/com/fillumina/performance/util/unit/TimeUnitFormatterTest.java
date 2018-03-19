package com.fillumina.performance.util.unit;

import java.util.Arrays;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati
 */
public class TimeUnitFormatterTest {
    private static final double SECOND = 1;
    private static final double MINUTE = 60.0 * SECOND;
    private static final double HOUR = 60.0 * MINUTE;

    @Test
    public void shouldSelectSeconds() {
        assertTimeUnit(AverageTimeUnit.SECONDS, 2, 3, 1, 8);
        assertTimeUnit(AverageTimeUnit.SECONDS, 2, 3, 1, 8, 102);
        assertTimeUnit(AverageTimeUnit.SECONDS, 12, 7, 20, 800);
        assertTimeUnit(AverageTimeUnit.SECONDS, 123, 7, 1340);
    }

    @Test
    public void shouldSelectMicroseconds() {
        assertTimeUnit(AverageTimeUnit.MICROSECONDS, 2.3E-6, 1E-6, 212E-6);
        assertTimeUnit(AverageTimeUnit.MICROSECONDS, 12.300E-6, 1.000E-6, 212E-6);
        assertTimeUnit(AverageTimeUnit.MICROSECONDS, 123.300E-6, 1E-6, 1.212E-6);
    }

    @Test
    public void shouldSelectMilliseconds() {
        assertTimeUnit(AverageTimeUnit.MILLISECONDS, 2.3E-3, 1E-3, 212E-3);
        assertTimeUnit(AverageTimeUnit.MILLISECONDS, 12.300E-3, 1.000E-3, 212E-3);
        assertTimeUnit(AverageTimeUnit.MILLISECONDS, 123.300E-3, 1E-3, 1.212E-3);
    }

    @Test
    public void shouldSelectNanoseconds() {
        assertTimeUnit(AverageTimeUnit.NANOSECONDS, 2.3E-9, 1E-9, 212E-9);
        assertTimeUnit(AverageTimeUnit.NANOSECONDS, 12.300E-9, 1.000E-9, 212E-9);
        assertTimeUnit(AverageTimeUnit.NANOSECONDS, 123.300E-9, 1E-9, 1.212E-9);
    }

    @Test
    public void shouldSelectMinutes() {
        assertTimeUnit(AverageTimeUnit.MINUTES, 3.6 * MINUTE, 1 * HOUR);
    }

    @Test
    public void shouldSelectHours() {
        assertTimeUnit(AverageTimeUnit.HOURS, HOUR * 4, HOUR);
    }

    private void assertTimeUnit(final AverageTimeUnit expected,
            double... values) {
        Unit<?> result = AverageTimeUnit.SECONDS.bestUnit(values);
        assertEquals(" values: " + Arrays.toString(values),
                expected, result);
    }
}
