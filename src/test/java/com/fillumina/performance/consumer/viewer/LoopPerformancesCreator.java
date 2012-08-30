package com.fillumina.performance.consumer.viewer;

import com.fillumina.performance.producer.timer.LoopPerformances;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author fra
 */
public class LoopPerformancesCreator {

    public static LoopPerformances parse(final int iterations,
            final Object[][] data) {
        final Map<String, Long> map = new LinkedHashMap<>();
        for (Object[] perf: data) {
            final String name = (String) perf[0];
            final long elapsed = (int) perf[1];
            map.put(name, elapsed);
        }
        return new LoopPerformances(iterations, map);
    }
}
