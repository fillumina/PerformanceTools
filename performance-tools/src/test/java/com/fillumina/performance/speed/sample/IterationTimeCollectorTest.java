package com.fillumina.performance.speed.sample;

import com.fillumina.performance.infrastructure.TN;
import com.fillumina.performance.util.TName;
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
        IterationTimeCollector collector = new IterationTimeCollector();
        collector.add(ONE, 100, 5);
        collector.add(ONE, 100, 5);

        SpeedSample sample = collector.createPerformanceSample();
        IterationTime iterationTime = sample.getTimeMap().get(ONE);

        assertEquals(200, iterationTime.getTimeNs());
        assertEquals(10, iterationTime.getIterations());
        assertEquals(20.0, iterationTime.getTimePerIterationNs(), 0);
    }

    @Test
    public void shouldAccountTimesOnASingleTest() {
        IterationTimeCollector collector = new IterationTimeCollector();
        collector.add(ONE, 100, 5);

        SpeedSample sample = collector.createPerformanceSample();
        IterationTime iterationTime = sample.getTimeMap().get(ONE);

        assertEquals(100, iterationTime.getTimeNs());
        assertEquals(5, iterationTime.getIterations());
        assertEquals(20.0, iterationTime.getTimePerIterationNs(), 0);
    }

    @Test
    public void shouldAccountForDifferentTimesForTheSameTest() {
        IterationTimeCollector collector = new IterationTimeCollector();
        collector.add(ONE, 100, 5);
        collector.add(ONE, 200, 10);

        SpeedSample sample = collector.createPerformanceSample();
        IterationTime iterationTime = sample.getTimeMap().get(ONE);

        assertEquals(300, iterationTime.getTimeNs());
        assertEquals(15, iterationTime.getIterations());
        assertEquals(20.0, iterationTime.getTimePerIterationNs(), 0);
    }

    @Test
    public void shouldAccountForDifferentTests() {
        IterationTimeCollector collector = new IterationTimeCollector();
        collector.add(ONE, 100, 5);
        collector.add(TWO, 200, 20);

        SpeedSample sample = collector.createPerformanceSample();

        IterationTime one = sample.getTimeMap().get(ONE);
        assertEquals(100, one.getTimeNs());
        assertEquals(5, one.getIterations());
        assertEquals(20.0, one.getTimePerIterationNs(), 0);

        IterationTime two = sample.getTimeMap().get(TWO);
        assertEquals(200, two.getTimeNs());
        assertEquals(20, two.getIterations());
        assertEquals(10.0, two.getTimePerIterationNs(), 0);
    }

}
