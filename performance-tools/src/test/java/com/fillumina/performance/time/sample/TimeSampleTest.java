package com.fillumina.performance.time.sample;

import com.fillumina.performance.executor.PN;
import com.fillumina.performance.executor.sample.Sample;
import com.fillumina.performance.executor.sample.SampleValue;
import com.fillumina.performance.mock.MockStatsType;
import com.fillumina.performance.util.pathname.PathName;
import com.fillumina.performance.util.pathname.PathNamedMap;
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
public class TimeSampleTest {
    private static final IntervalUnit UNIT = IntervalUnit.MILLISECONDS;

    private static final int ELAPSED_TWO = 2_500;
    private static final int ELAPSED_ONE = 10_000;

    private static final PathName TWO = PN.pname("two");
    private static final PathName ONE = PN.pname("one");

    private PathNamedMap<SampleValue> map;
    private Sample sample;

    @Before
    public void initMap() {
        this.map = new PathNamedMap<>();
        map.add(new SampleValue(ONE, ELAPSED_ONE, UNIT));
        map.add(new SampleValue(TWO, ELAPSED_TWO, UNIT));

        this.sample = new Sample(MockStatsType.INSTANCE, map);
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
    public void shouldReturnTheValues() {
        assertEquals(ELAPSED_ONE,
                sample.getQuantity(ONE).as(IntervalUnit.MILLISECONDS), 0.01);
        assertEquals(ELAPSED_TWO,
                sample.getQuantity(TWO).as(IntervalUnit.MILLISECONDS), 0.01);
    }
}
