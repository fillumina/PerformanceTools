package com.fillumina.performance.time.sample;

import com.fillumina.performance.executor.PN;
import com.fillumina.performance.executor.sample.Sample;
import com.fillumina.performance.executor.sample.SampleValue;
import com.fillumina.performance.util.pathname.PathName;
import com.fillumina.performance.util.unit.AverageTimeUnit;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class IterationTimeCollectorTest {

    private static final PathName ONE = PN.pname("one");
    private static final PathName TWO = PN.pname("two");

    @Test
    public void shouldAccountForDifferentMeasuresOfTheSameTest() {
        TimeSampleCollector collector = new TimeSampleCollector();
        collector.add(ONE, 100, 5);
        collector.add(ONE, 100, 5);

        Sample sample = collector.buildAverageTimeSample();

        SampleValue value = sample.getValuesMap().get(ONE);

        assertEquals(sample.toString(),
                (100.0 / 5.0 + 100.0 / 5.0) / 2.0,
                value.getQuantity().as(AverageTimeUnit.NANOSECONDS), 1E-9);
    }

    @Test
    public void shouldAccountTimesOnASingleTest() {
        TimeSampleCollector collector = new TimeSampleCollector();
        collector.add(ONE, 100, 5);

        Sample sample = collector.buildAverageTimeSample();
        SampleValue value = sample.getValuesMap().get(ONE);

        assertEquals(sample.toString(),
                100.0 / 5.0,
                value.getQuantity().as(AverageTimeUnit.NANOSECONDS), 1E-9);
    }

    @Test
    public void shouldAccountForDifferentTimesForTheSameTest() {
        TimeSampleCollector collector = new TimeSampleCollector();
        collector.add(ONE, 100, 5);
        collector.add(ONE, 200, 10);

        Sample sample = collector.buildAverageTimeSample();
        SampleValue value = sample.getValuesMap().get(ONE);

        assertEquals(sample.toString(),
                (100.0 / 5.0 + 200.0 / 10.0) / 2.0,
                value.getQuantity().as(AverageTimeUnit.NANOSECONDS), 1E-9);
    }

    @Test
    public void shouldAccountForDifferentTests() {
        TimeSampleCollector collector = new TimeSampleCollector();
        collector.add(ONE, 100, 5);
        collector.add(TWO, 200, 20);

        Sample sample = collector.buildAverageTimeSample();

        SampleValue one = sample.getValuesMap().get(ONE);
        assertEquals(sample.toString(),
                100.0 / 5.0,
                one.getQuantity().as(AverageTimeUnit.NANOSECONDS), 1E-9);

        SampleValue two = sample.getValuesMap().get(TWO);
        assertEquals(sample.toString(),
                200.0 / 20.0,
                two.getQuantity().as(AverageTimeUnit.NANOSECONDS), 1E-9);
    }

}
