package com.fillumina.performance.time.sample;

import com.fillumina.performance.util.TName;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Collects {@link IterationTime}s and creates a {@link SpeedSample}
 * out of them.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class IterationTimeCollector {
    private final Map<TName, IterationTime> timeMap;
    private long totalTime;
    private int totalIterations;

    public IterationTimeCollector() {
        this.timeMap = new LinkedHashMap<>();
    }

    public IterationTimeCollector add(final TName name,
            final long elapsed, final int iterations) {
        IterationTimeAccumulator time = (IterationTimeAccumulator)
                timeMap.get(name);
        if (time == null) {
            time = new IterationTimeAccumulator();
            timeMap.put(name, time);
        }
        time.add(elapsed, iterations);
        totalIterations += iterations;
        return this;
    }

    public long getTotalTime() {
        return totalTime;
    }

    public int getTotalIterations() {
        return totalIterations;
    }

    /**
     * Returns a snapshot of the collected samples.
     *
     * @return the SpeedSample of the collected samples.
     */
    public SpeedSample createPerformanceSample() {
        return new SpeedSample(timeMap);
    }
}
