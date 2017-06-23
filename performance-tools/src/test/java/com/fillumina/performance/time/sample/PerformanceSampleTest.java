package com.fillumina.performance.time.sample;

import com.fillumina.performance.time.sample.IterationTimeCollector;
import com.fillumina.performance.time.sample.SpeedSample;
import com.fillumina.performance.time.sample.IterationTime;
import com.fillumina.performance.infrastructure.TN;
import com.fillumina.performance.util.TName;
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
    private static final TName THIRD = TN.tname("third");
    private static final TName SECOND = TN.tname("second");
    private static final TName FIRST = TN.tname("first");

    private SpeedSample sample;

    @Before
    public void initLoopPerformance() {
        sample = new IterationTimeCollector()
                .add(FIRST, 500L, ITERATIONS)
                .add(SECOND, 1000L, ITERATIONS)
                .add(THIRD, 1500L, ITERATIONS)
                .createPerformanceSample();
    }

    @Test
    public void shouldGetTheSize() {
        assertEquals(3, sample.getTimeMap().size());
    }

    @Test
    public void shouldGetTheIterationsNumber() {
        final Map<TName, IterationTime> timeMap = sample.getTimeMap();
        assertEquals(ITERATIONS, timeMap.get(FIRST).getIterations());
        assertEquals(ITERATIONS, timeMap.get(SECOND).getIterations());
        assertEquals(ITERATIONS, timeMap.get(THIRD).getIterations());
    }

    @Test
    public void shouldHonourTheListOrder() {
        Iterator<TName> it = sample.getTimeMap().keySet().iterator();
        assertEquals(FIRST, it.next());
        assertEquals(SECOND, it.next());
        assertEquals(THIRD, it.next());
    }

    @Test
    public void shouldReturnTheTotalTime() {
        assertEquals(500 + 1000 + 1500, sample.getTotalTimeNs());
    }
}
