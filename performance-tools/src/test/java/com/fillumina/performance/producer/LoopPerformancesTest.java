package com.fillumina.performance.producer;

import com.fillumina.performance.util.Statistics;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

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
        assertEquals(3, loopPerformances.numberOfTests());
    }

    @Test
    public void shouldGetTheIterationsNumber() {
        assertEquals(ITERATIONS, loopPerformances.getIterations());
    }

    @Test
    public void shouldReturnTheTestPerformances() {
        final TestPerformances tp = loopPerformances
                .getPerformancesFor("second");
        assertEquals(1000L, tp.getElapsedNanoseconds());
        assertEquals(1000L / ITERATIONS, tp.getElapsedNanosecondsPerCycle(), 1E-3);
        assertEquals("second", tp.getName());
        assertEquals(66.666, tp.getPercentage(), 1E-3);
    }

    @Test
    public void shouldHonourTheListOrder() {
        assertEquals("first",
                loopPerformances.getNameList().get(0));


        assertEquals("second",
                loopPerformances.getNameList().get(1));
        assertEquals(1000L,
                loopPerformances.getElapsedNanosecondsList().get(1), 1E-3);
        assertEquals(1000L / ITERATIONS,
                loopPerformances.getNanosecondsPerCycleList().get(1), 1E-3);
        assertEquals(66.666,
                loopPerformances.getPercentageList().get(1), 1E-3);

        assertEquals("third",
            loopPerformances.getNameList().get(2));
    }

    @Test
    public void shouldGiveCorrectStatistics() {
        final Statistics stats = loopPerformances.getStatistics();
        assertEquals(1000D, stats.average(), 1E-3);
    }
}
