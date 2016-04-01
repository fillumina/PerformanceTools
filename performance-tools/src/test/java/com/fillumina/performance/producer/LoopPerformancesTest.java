package com.fillumina.performance.producer;

import com.fillumina.performance.stats.Measure;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati
 */
public class LoopPerformancesTest {
    public static final int ITERATIONS = 1_000;

    private LoopPerformances loopPerformances;

    @Before
    public void initLoopPerformance() {
        final Map<String,Long> timeMap = new LinkedHashMap<>();
        timeMap.put("first", 500L);
        timeMap.put("second", 1000L);
        timeMap.put("third", 1500L);

        loopPerformances = new LoopPerformances(ITERATIONS, timeMap);
    }

    @Test
    public void shouldGetTheSize() {
        assertEquals(3, loopPerformances.getNumberOfTests());
    }

    @Test
    public void shouldGetTheIterationsNumber() {
        assertEquals(ITERATIONS, loopPerformances.getIterations());
    }

    @Test
    public void shouldReturnTheTestPerformances() {
        final TestPerformances tp = loopPerformances
                .getPerformances().get("second");
        assertEquals(1000L, tp.getElapsedNanoseconds());
        assertEquals(1000L / ITERATIONS, tp.getElapsedNanosecondsPerCycle(), 1E-3);
        assertEquals("second", tp.getName());
        assertEquals(66.666, tp.getPercentage(), 1E-3);
    }

    @Test
    public void shouldHonourTheListOrder() {
        Iterator<TestPerformances> it = loopPerformances.getTests().iterator();
        assertEquals("first", it.next().getName());
        assertEquals("second", it.next().getName());
        assertEquals("third", it.next().getName());
    }

    @Test
    public void shouldReturnTheStatistics() {
        TestPerformances tp = loopPerformances.getPerformances().get("second");
        assertEquals("second", tp.getName());
        assertEquals(1000L, tp.getElapsedNanoseconds(), 1E-3);
        assertEquals(1000L / ITERATIONS,
                tp.getElapsedNanosecondsPerCycle(), 1E-3);
        assertEquals(66.666, tp.getPercentage(), 1E-3);
    }

    @Test
    public void shouldGiveCorrectStatistics() {
        final Measure stats = loopPerformances.getStatistics();
        assertEquals(1000D, stats.mean(), 1E-3);
    }
}
