package com.fillumina.performance.util.unit;

import java.util.Arrays;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class IntervalUnitTest {

    @Test
    public void shouldConvertFromNsToSeconds() {
        double sec = AverageTimeUnit.SECONDS.convertFromBase(1E9);
        assertEquals(1, sec, 0);
    }

    @Test
    public void shouldConvertMillis() {
       double seconds = AverageTimeUnit.SECONDS.convert(1, AverageTimeUnit.MILLISECONDS);
       assertEquals(0.001, seconds, 0);
    }

    @Test
    public void shouldConvertDays() {
       double seconds = AverageTimeUnit.SECONDS.convert(1, AverageTimeUnit.DAYS);
       assertEquals(3600 * 24, seconds, 0);
    }

    @Test
    public void shouldReturnTheSymbol() {
        assertEquals("ns", AverageTimeUnit.NANOSECONDS.toString());
        assertEquals("us", AverageTimeUnit.MICROSECONDS.toString());
        assertEquals("ms", AverageTimeUnit.MILLISECONDS.toString());
        assertEquals("s", AverageTimeUnit.SECONDS.toString());
        assertEquals("m", AverageTimeUnit.MINUTES.toString());
        assertEquals("h", AverageTimeUnit.HOURS.toString());
        assertEquals("d", AverageTimeUnit.DAYS.toString());
    }

    @Test
    public void shouldReturnTheListOfAvailableUnits() {
        assertEquals(Arrays.asList(
                    AverageTimeUnit.NANOSECONDS,
                    AverageTimeUnit.MICROSECONDS,
                    AverageTimeUnit.MILLISECONDS,
                    AverageTimeUnit.SECONDS,
                    AverageTimeUnit.MINUTES,
                    AverageTimeUnit.HOURS,
                    AverageTimeUnit.DAYS),
                Arrays.asList(AverageTimeUnit.values()));
    }

    @Test
    public void shouldFindTheRightUnit() {
        Unit dimension = AverageTimeUnit.UNITS.calculateAppropriatedUnit(12.23E6);
        assertEquals(AverageTimeUnit.MILLISECONDS, dimension);
    }

    @Test
    public void shouldFormatStatically() {
        assertEquals("123.4568 ms", AverageTimeUnit.UNITS.toString(0.123456789E9));
    }

    @Test
    public void shouldFormatStaticallyByHelper() {
        assertEquals("123.4568 ms",
                new Units<>(AverageTimeUnit.values()).toString(0.123456789E9));
    }

    @Test
    public void shouldPrettyFormatWith1Unit() {
        assertEquals("123 ms",
                AverageTimeUnit.UNITS.toPrettyString(0.123456789E9, 1));
    }

    @Test
    public void shouldPrettyFormatWith2Units() {
        assertEquals("123 ms 456 us",
                AverageTimeUnit.UNITS.toPrettyString(0.123456789E9, 2));
    }

    @Test
    public void shouldPrettyFormatWith3Units() {
        assertEquals("123 ms 456 us 789 ns",
                AverageTimeUnit.UNITS.toPrettyString(0.123456789E9, 3));
    }

    @Test
    public void shouldFormatFromGivenUnit() {
        assertEquals("0.1235 s",
                Units.toString(0.123456789E9, 4, AverageTimeUnit.SECONDS));
    }
}
