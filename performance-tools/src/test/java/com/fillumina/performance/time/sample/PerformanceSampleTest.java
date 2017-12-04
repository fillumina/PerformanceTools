package com.fillumina.performance.time.sample;

import com.fillumina.performance.executor.TN;
import com.fillumina.performance.executor.sample.Sample;
import com.fillumina.performance.util.tname.TName;
import java.util.Iterator;
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

    private Sample sample;

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
    public void shouldHonourTheListOrder() {
        Iterator<TName> it = sample.getValuesMap().keySet().iterator();
        assertEquals(FIRST, it.next());
        assertEquals(SECOND, it.next());
        assertEquals(THIRD, it.next());
    }
}
