package com.fillumina.performance.sample;

import java.util.Iterator;
import java.util.Map;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati
 */
public class PerformanceSampleTest {
    private static final int ITERATIONS = 1_000;
    private static final String THIRD = "third";
    private static final String SECOND = "second";
    private static final String FIRST = "first";

    private PerformanceSample sample;

    @Before
    public void initLoopPerformance() {
        sample = new PerformanceSample()
                .add(FIRST, 500L, ITERATIONS)
                .add(SECOND, 1000L, ITERATIONS)
                .add(THIRD, 1500L, ITERATIONS);
    }

    @Test
    public void shouldGetTheSize() {
        assertEquals(3, sample.getTimeMap().size());
    }

    @Test
    public void shouldGetTheIterationsNumber() {
        final Map<String, IterationTime> timeMap = sample.getTimeMap();
        assertEquals(ITERATIONS, timeMap.get(FIRST).getIterations());
        assertEquals(ITERATIONS, timeMap.get(SECOND).getIterations());
        assertEquals(ITERATIONS, timeMap.get(THIRD).getIterations());
    }

    @Test
    public void shouldHonourTheListOrder() {
        Iterator<String> it = sample.getTimeMap().keySet().iterator();
        assertEquals(FIRST, it.next());
        assertEquals(SECOND, it.next());
        assertEquals(THIRD, it.next());
    }
}
