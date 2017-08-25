package com.fillumina.performance.time.sample;

import com.fillumina.performance.time.sample.IterationTimeAccumulator;
import com.fillumina.performance.time.sample.AverageTimeSample;
import com.fillumina.performance.time.sample.IterationTime;
import com.fillumina.performance.infrastructure.TN;
import com.fillumina.performance.util.tname.TName;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.MeasureRatio;
import com.fillumina.performance.util.stats.Ratio;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
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
    private static final int ITERATION_TWO = 50;
    private static final int ELAPSED_TWO = 2_500;
    private static final int ITERATION_ONE = 100;
    private static final int ELAPSED_ONE = 10_000;

    private static final TName TWO = TN.tname("two");
    private static final TName ONE = TN.tname("one");

    private Map<TName,IterationTime> map;
    private AverageTimeSample sample;

    @Before
    public void initMap() {
        this.map = new LinkedHashMap<>();
        map.put(ONE, new IterationTimeAccumulator(ELAPSED_ONE, ITERATION_ONE));
        map.put(TWO, new IterationTimeAccumulator(ELAPSED_TWO, ITERATION_TWO));

        this.sample = new AverageTimeSample(map);
    }

    @Test
    public void shouldReturnInsertedNames() {
        final Collection<TName> names = sample.getTestNames();
        assertEquals(2, names.size());
        assertTrue(names.contains(ONE));
        assertTrue(names.contains(TWO));
    }

    @Test
    public void shouldTheTimeMapBeASafeCopyOfTheGivenOne() {
        assertEquals(map, sample.getTimeMap());
        assertNotSame(map, sample.getTimeMap());
    }

    @Test
    public void shouldCalculateTheTotalTime() {
        assertEquals(12_500, sample.getTotalTimeNs());
    }

    @Test
    public void shouldReturnTheMeasures() {
        Measure one = sample.getMeasure(ONE);
        assertEquals(ELAPSED_ONE/ITERATION_ONE, one.getMean(), 0.01);
        assertEquals(0, one.getStandardDeviation(), 0.01);

        Measure two = sample.getMeasure(TWO);
        assertEquals(ELAPSED_TWO/ITERATION_TWO, two.getMean(), 0.01);
        assertEquals(0, two.getStandardDeviation(), 0.01);
    }

    @Test
    public void shouldReturnTheRatiosBetweenTests() {
        MeasureRatio ratio = sample.getRatioToReferenceTest(TWO, Ratio.P_95);
        double speedOne = ELAPSED_ONE * 1.0 / ITERATION_ONE;
        double speedTwo = ELAPSED_TWO * 1.0 / ITERATION_TWO;
        assertEquals(speedTwo / speedOne, ratio.getValue(), 0.01);
    }
}
