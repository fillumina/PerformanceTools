package com.fillumina.performance.speed.sample;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Collects {@link IterationTime}s and creates a {@link SpeedSample}
 * out of them.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class IterationTimeCollector {
    private final Map<String, IterationTimeAccumulator> timeMap;

    public IterationTimeCollector() {
        this.timeMap = new LinkedHashMap<>();
    }

    public IterationTimeCollector add(final String name,
            final long elapsed, final long iterations) {
        IterationTimeAccumulator time = timeMap.get(name);
        if (time == null) {
            time = new IterationTimeAccumulator();
            timeMap.put(name, time);
        }
        time.add(elapsed, iterations);
        return this;
    }

    /**
     * Returns a snapshot of the collected samples.
     *
     * @return the SpeedSample of the collected samples.
     */
    public SpeedSample createPerformanceSample() {
        return new SpeedSample(calculateTotalTime(),
            Collections.unmodifiableMap(
                (Map<String, ? extends IterationTime>)timeMap));
    }

    private long calculateTotalTime() {
        long total = 0;
        for (IterationTimeAccumulator ti : timeMap.values()) {
            total += ti.getTime();
        }
        return total;
    }
}
