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
        double sec = IntervalUnit.SECONDS.convertFromBase(1E9);
        assertEquals(1, sec, 0);
    }

    @Test
    public void shouldConvertMillis() {
       double seconds = IntervalUnit.SECONDS.convert(1,
               IntervalUnit.MILLISECONDS);
       assertEquals(0.001, seconds, 0);
    }

    @Test
    public void shouldConvertDays() {
       double seconds = IntervalUnit.SECONDS.convert(1,
               IntervalUnit.DAYS);
       assertEquals(3600 * 24, seconds, 0);
    }

    @Test
    public void shouldReturnTheSymbol() {
        assertEquals("ns", IntervalUnit.NANOSECONDS.toString());
        assertEquals("us", IntervalUnit.MICROSECONDS.toString());
        assertEquals("ms", IntervalUnit.MILLISECONDS.toString());
        assertEquals("s", IntervalUnit.SECONDS.toString());
        assertEquals("m", IntervalUnit.MINUTES.toString());
        assertEquals("h", IntervalUnit.HOURS.toString());
        assertEquals("d", IntervalUnit.DAYS.toString());
    }

    @Test
    public void shouldReturnTheListOfAvailableUnits() {
        assertEquals(Arrays.asList(
                    IntervalUnit.NANOSECONDS,
                    IntervalUnit.MICROSECONDS,
                    IntervalUnit.MILLISECONDS,
                    IntervalUnit.SECONDS,
                    IntervalUnit.MINUTES,
                    IntervalUnit.HOURS,
                    IntervalUnit.DAYS),
                IntervalUnit.INSTANCE.LIST);
    }

    @Test
    public void shouldReturnTheListOfAllAvailableMeasures() {
        assertEquals(IntervalUnit.INSTANCE.LIST,
                IntervalUnit.INSTANCE.allAvailableUnitOfMeasures());
    }

    @Test
    public void shouldReturnTheBase() {
        assertEquals(IntervalUnit.NANOSECONDS, IntervalUnit.INSTANCE.getBase());
    }

    @Test
    public void shouldFindTheRightUnit() {
        IntervalUnit unit = IntervalUnit.FORMATTER.getUnit(12.23E6);
        assertEquals(IntervalUnit.MILLISECONDS, unit);
    }

    @Test
    public void shouldFormat() {
        assertEquals("0.1235 s",
                IntervalUnit.FORMATTER.toString(
                        0.123456789E9, 4, IntervalUnit.SECONDS));
    }
}
