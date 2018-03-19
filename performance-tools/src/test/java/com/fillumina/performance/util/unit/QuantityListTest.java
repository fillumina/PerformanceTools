package com.fillumina.performance.util.unit;

import java.util.List;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class QuantityListTest {

    @Test
    public void shouldArmonizeDifferentUnits() {
        QuantityList armonizer = QuantityList.builder()
                .add(100, IntervalUnit.SECONDS)
                .add(0.2, IntervalUnit.MINUTES)
                .add(0.003, IntervalUnit.HOURS)
                .add(183, IntervalUnit.MILLISECONDS)
                .build();

        assertEquals(IntervalUnit.SECONDS, armonizer.getUnit());
    }

    @Test
    public void shouldArmonizeEqualUnits() {
        QuantityList armonizer = QuantityList.builder()
                .add(100, IntervalUnit.MILLISECONDS)
                .add(2_000, IntervalUnit.MILLISECONDS)
                .add(130_123, IntervalUnit.MILLISECONDS)
                .add(783, IntervalUnit.MILLISECONDS)
                .build();

        assertEquals(IntervalUnit.SECONDS, armonizer.getUnit());
    }

    @Test
    public void shouldReturnAValueList() {
        List<Double> list = QuantityList.builder()
                .add(100, IntervalUnit.MILLISECONDS)
                .add(2_000, IntervalUnit.MILLISECONDS)
                .add(130_123, IntervalUnit.MILLISECONDS)
                .add(783, IntervalUnit.MILLISECONDS)
                .build();

        assertEquals(0.1, list.get(0), 0);
        assertEquals(2.0, list.get(1), 0);
        assertEquals(130.123, list.get(2), 0);
        assertEquals(0.783, list.get(3), 0);
    }

    @Test(expected = MismatchedUnitRuntimeException.class)
    public void shouldNotAcceptDifferentUnits() {
        QuantityList.builder()
                .add(100, IntervalUnit.MILLISECONDS)
                .add(2_000, AverageTimeUnit.MILLISECONDS)
                .build();
    }
}
