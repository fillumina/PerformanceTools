package com.fillumina.performance.timer;

import com.fillumina.performance.producer.timer.LoopPerformances;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author fra
 */
public class LoopPerformancesTest {
    public static final int ITERATIONS = 1_000;

    private LoopPerformances loopPerformances;

    @Before
    public void initLoopPerformance() {
        final Map<String,Long> timeMap = new HashMap<>();
        timeMap.put("first", 500L);
        timeMap.put("sedond", 1000L);
        timeMap.put("third", 1500L);

        loopPerformances = new LoopPerformances(ITERATIONS, timeMap);
    }

    @Test
    public void shouldGetTheSize() {
        assertEquals(3, loopPerformances.size());
    }

}
