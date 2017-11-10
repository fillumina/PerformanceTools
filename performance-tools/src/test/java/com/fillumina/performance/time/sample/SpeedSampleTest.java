package com.fillumina.performance.time.sample;

import com.fillumina.performance.executor.TN;
import com.fillumina.performance.util.tname.TName;
import com.fillumina.performance.util.tname.TNameMap;
import com.fillumina.performance.util.unit.IntervalUnit;
import java.util.Collection;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SpeedSampleTest {
    private static final IntervalUnit UNIT = IntervalUnit.MILLISECONDS;

    private static final int ITERATION_TWO = 50;
    private static final int ELAPSED_TWO = 2_500;
    private static final int ITERATION_ONE = 100;
    private static final int ELAPSED_ONE = 10_000;

    private static final TName TWO = TN.tname("two");
    private static final TName ONE = TN.tname("one");

    private TNameMap<TimeSampleValue> map;
    private AverageTimeSample sample;

    @Before
    public void initMap() {
        this.map = new TNameMap<>();
        map.add(new TimeSampleValue(
                ONE, ELAPSED_ONE, UNIT, "average", ITERATION_ONE, 500));
        map.add(new TimeSampleValue(
                TWO, ELAPSED_TWO, UNIT, "average", ITERATION_TWO, 200));

        this.sample = new AverageTimeSample(map, 3_000);
    }

    @Test
    public void shouldReturnInsertedNames() {
        final Collection<? extends CharSequence> names = sample.getTestNames();
        assertEquals(2, names.size());
        assertTrue(names.contains(ONE));
        assertTrue(names.contains(TWO));
    }

    @Test
    public void shouldTheTimeMapBeASafeCopyOfTheGivenOne() {
        assertEquals(map, sample.getValuesMap());
        assertNotSame(map, sample.getValuesMap());
    }

    @Test
    public void shouldCalculateTheTotalTime() {
        assertEquals(3_000, sample.getTotalTimeNs());
    }

    @Test
    public void shouldReturnTheValues() {
        assertEquals(ELAPSED_ONE, sample.getValue(ONE), 0.01);
        assertEquals(ELAPSED_TWO, sample.getValue(TWO), 0.01);
    }
}
