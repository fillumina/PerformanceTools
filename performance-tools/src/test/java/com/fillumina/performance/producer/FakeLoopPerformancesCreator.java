package com.fillumina.performance.producer;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati
 */
public class FakeLoopPerformancesCreator {

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
