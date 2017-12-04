package com.fillumina.performance.time.sample;

import com.fillumina.performance.executor.TN;
import com.fillumina.performance.executor.sample.Sample;
import com.fillumina.performance.executor.sample.SampleValue;
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

        Sample sample = collector.buildAverageTimeSample();

        SampleValue value = sample.getValuesMap().get(ONE);

        assertEquals(sample.toString(),
                (100.0 / 5.0 + 100.0 / 5.0) / 2.0,
                value.getQuantity().toBase(), 0);
    }

    @Test
    public void shouldAccountTimesOnASingleTest() {
        TimeSampleCollector collector = new TimeSampleCollector();
        collector.add(ONE, 100, 5);

        Sample sample = collector.buildAverageTimeSample();
        SampleValue value = sample.getValuesMap().get(ONE);

        assertEquals(sample.toString(),
                100.0 / 5.0,
                value.getQuantity().toBase(), 0);
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
                value.getQuantity().toBase(), 0);
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
                one.getQuantity().toBase(), 0);

        SampleValue two = sample.getValuesMap().get(TWO);
        assertEquals(sample.toString(),
                200.0 / 20.0,
                two.getQuantity().toBase(), 0);
    }

}
