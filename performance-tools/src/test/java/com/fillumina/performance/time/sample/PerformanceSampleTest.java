package com.fillumina.performance.time.sample;

import com.fillumina.performance.executor.TN;
import com.fillumina.performance.util.tname.TName;
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

    private AverageTimeSample sample;

    @Before
    public void initLoopPerformance() {
        sample = new TimeSampleCollector()
                .add(FIRST, 500L, ITERATIONS)
                .add(SECOND, 1000L, ITERATIONS)
                .add(THIRD, 1500L, ITERATIONS)
                .buildAverageTimeSample();
    }

    @Test
    public void shouldGetTheSize() {
        assertEquals(3, sample.getValuesMap().size());
    }

    @Test
    public void shouldGetTheIterationsNumber() {
        final Map<TName, SampleValue> timeMap = sample.getValuesMap();
        assertEquals(ITERATIONS, timeMap.get(FIRST).getIterations());
        assertEquals(ITERATIONS, timeMap.get(SECOND).getIterations());
        assertEquals(ITERATIONS, timeMap.get(THIRD).getIterations());
    }

    @Test
    public void shouldHonourTheListOrder() {
        Iterator<TName> it = sample.getValuesMap().keySet().iterator();
        assertEquals(FIRST, it.next());
        assertEquals(SECOND, it.next());
        assertEquals(THIRD, it.next());
    }

    @Test
    public void shouldReturnTheTotalTime() {
        assertEquals(500 + 1000 + 1500, sample.getTotalTimeNs());
    }
}
