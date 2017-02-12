package com.fillumina.performance.speed.sample;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class IterationTimeCollectorTest {

    @Test
    public void shouldAccountTimesOnASingleTest() {
        IterationTimeCollector collector = new IterationTimeCollector();
        collector.add("one", 100, 5);

        SpeedSample sample = collector.createPerformanceSample();
        IterationTime iterationTime = sample.getTimeMap().get("one");

        assertEquals(100, iterationTime.getTimeNs());
        assertEquals(5, iterationTime.getIterations());
        assertEquals(20.0, iterationTime.getTimePerIterationNs(), 0);
    }

    @Test
    public void shouldAccountForDifferentTimesForTheSameTest() {
        IterationTimeCollector collector = new IterationTimeCollector();
        collector.add("one", 100, 5);
        collector.add("one", 200, 10);

        SpeedSample sample = collector.createPerformanceSample();
        IterationTime iterationTime = sample.getTimeMap().get("one");

        assertEquals(300, iterationTime.getTimeNs());
        assertEquals(15, iterationTime.getIterations());
        assertEquals(20.0, iterationTime.getTimePerIterationNs(), 0);
    }

    @Test
    public void shouldAccountForDifferentTests() {
        IterationTimeCollector collector = new IterationTimeCollector();
        collector.add("one", 100, 5);
        collector.add("two", 200, 20);

        SpeedSample sample = collector.createPerformanceSample();

        IterationTime one = sample.getTimeMap().get("one");
        assertEquals(100, one.getTimeNs());
        assertEquals(5, one.getIterations());
        assertEquals(20.0, one.getTimePerIterationNs(), 0);

        IterationTime two = sample.getTimeMap().get("two");
        assertEquals(200, two.getTimeNs());
        assertEquals(20, two.getIterations());
        assertEquals(10.0, two.getTimePerIterationNs(), 0);
    }

}
