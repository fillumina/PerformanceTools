package com.fillumina.performance.time.sample;

import com.fillumina.performance.executor.TN;
import com.fillumina.performance.util.tname.TName;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class IterationTimeCollectorTest {

    private static final TName ONE = TN.tname("one");
    private static final TName TWO = TN.tname("two");

    @Test
    public void shouldAccountForDifferentMeasuresOfTheSameTest() {
        TimeSampleCollector collector = new TimeSampleCollector();
        collector.add(ONE, 100, 5);
        collector.add(ONE, 100, 5);

        AverageTimeSample sample = collector.buildAverageTimeSample();
        TimeSampleValue value = sample.getValuesMap().get(ONE);

        assertEquals(200, value.getTimeNs());
        assertEquals(10, value.getIterations());
    }

    @Test
    public void shouldAccountTimesOnASingleTest() {
        TimeSampleCollector collector = new TimeSampleCollector();
        collector.add(ONE, 100, 5);

        AverageTimeSample sample = collector.buildAverageTimeSample();
        TimeSampleValue value = sample.getValuesMap().get(ONE);

        assertEquals(100, value.getTimeNs());
        assertEquals(5, value.getIterations());
    }

    @Test
    public void shouldAccountForDifferentTimesForTheSameTest() {
        TimeSampleCollector collector = new TimeSampleCollector();
        collector.add(ONE, 100, 5);
        collector.add(ONE, 200, 10);

        AverageTimeSample sample = collector.buildAverageTimeSample();
        TimeSampleValue value = sample.getValuesMap().get(ONE);

        assertEquals(300, value.getTimeNs());
        assertEquals(15, value.getIterations());
    }

    @Test
    public void shouldAccountForDifferentTests() {
        TimeSampleCollector collector = new TimeSampleCollector();
        collector.add(ONE, 100, 5);
        collector.add(TWO, 200, 20);

        AverageTimeSample sample = collector.buildAverageTimeSample();

        TimeSampleValue one = sample.getValuesMap().get(ONE);
        assertEquals(100, one.getTimeNs());
        assertEquals(5, one.getIterations());

        TimeSampleValue two = sample.getValuesMap().get(TWO);
        assertEquals(200, two.getTimeNs());
        assertEquals(20, two.getIterations());
    }

}
