package com.fillumina.performance;

import org.junit.Test;
import static org.junit.Assert.*;
import static com.fillumina.performance.utils.TimeUnitHelper.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author fra
 */
public class TimeUnitHelperTest {

    @Test
    public void shouldSelectTheMinimumTimeUnit() {
        assertTimeUnit(TimeUnit.NANOSECONDS, 2, 3, 1, 8);

        assertTimeUnit(TimeUnit.NANOSECONDS, 2, 3, 1, 8, 102000);

        assertTimeUnit(TimeUnit.NANOSECONDS, 12, 7, 0, 8);

        assertTimeUnit(TimeUnit.MICROSECONDS, 123, 700, 134);

        assertTimeUnit(TimeUnit.MICROSECONDS, 2300, 1000, 212000);

        assertTimeUnit(TimeUnit.MILLISECONDS, 10_000_000, 21_213_544);

        assertTimeUnit(TimeUnit.MILLISECONDS, 10_000_000, 21_213_544);

        assertTimeUnit(TimeUnit.SECONDS, 100_000_000D, 121_213_544D);

        assertTimeUnit(TimeUnit.MINUTES, 100_000_000_000L, 354_121_213_544L);

        assertTimeUnit(TimeUnit.HOURS, 1_000_000_000_000L, 7_354_121_213_544L);

        assertTimeUnit(TimeUnit.DAYS, 10_000_000_000_000L, 73_354_121_213_544L);
    }

    private void assertTimeUnit(final TimeUnit expected, double... values) {
        final Collection<Double> col = convert(values);
        final TimeUnit result = minTimeUnit(minMagnitude(col));
        assertEquals("expected: " + expected + ", found: " + result,
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
