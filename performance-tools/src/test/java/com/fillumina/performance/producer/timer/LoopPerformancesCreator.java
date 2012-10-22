package com.fillumina.performance.producer.timer;

import com.fillumina.performance.producer.LoopPerformances;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class LoopPerformancesCreator {

    public static LoopPerformances parse(final long iterations,
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
