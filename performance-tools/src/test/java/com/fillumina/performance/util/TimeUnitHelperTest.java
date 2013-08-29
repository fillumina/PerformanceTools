package com.fillumina.performance.util;

import org.junit.Test;
import static org.junit.Assert.*;
import static com.fillumina.performance.util.TimeUnitHelper.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class TimeUnitHelperTest {

    @Test
    public void shouldSelectTheMinimumTimeUnit() {
        assertTimeUnit(TimeUnit.NANOSECONDS, 2, 3, 1, 8);
        assertTimeUnit(TimeUnit.NANOSECONDS, 2, 3, 1, 8, 102000);
        assertTimeUnit(TimeUnit.NANOSECONDS, 12, 7, 0, 800);
        assertTimeUnit(TimeUnit.NANOSECONDS, 123, 700, 134);

        assertTimeUnit(TimeUnit.MICROSECONDS, 2_300, 1_000, 212_000);
        assertTimeUnit(TimeUnit.MICROSECONDS, 12_300, 1_000, 212_000);
        assertTimeUnit(TimeUnit.MICROSECONDS, 123_300, 1_000, 1_212_000);

        assertTimeUnit(TimeUnit.MILLISECONDS, 1_000_000, 21_213_544);
        assertTimeUnit(TimeUnit.MILLISECONDS, 10_000_000, 212_213_544);
        assertTimeUnit(TimeUnit.MILLISECONDS, 100_000_000D, 121_213_544D);

        assertTimeUnit(TimeUnit.SECONDS, 1_000_000_000L, 354_121_213L);
        assertTimeUnit(TimeUnit.SECONDS, 10_000_000_000L, 354_121_213L);

        assertTimeUnit(TimeUnit.MINUTES, 100_000_000_000L, 354_121_213_544L);

        assertTimeUnit(TimeUnit.HOURS, 1_000_000_000_000L, 7_354_121_213_544L);

        assertTimeUnit(TimeUnit.DAYS, 10_000_000_000_000L, 73_354_121_213_544L);
    }

    private void assertTimeUnit(final TimeUnit expected, double... values) {
        final Collection<Double> col = convert(values);
        final int magnitude = minMagnitude(col);
        final TimeUnit result = minTimeUnit(magnitude);
        assertEquals(" values: " + Arrays.toString(values) +
                " magnitude: " + magnitude +
                " expected: " + expected + ", found: " + result,
                expected, result);
    }

    private Collection<Double> convert(final double[] array) {
        final List<Double> list = new ArrayList<>(array.length);
        for (double l:array) {
            list.add(l);
        }
        return list;
    }
}
