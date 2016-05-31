package com.fillumina.performance.sample;

import com.fillumina.performance.sample.formatter.StringTableSampleFormatter;
import java.io.Serializable;
import java.util.*;

/**
 * Keeps the tests elapsed times. Each test might have been executed for a
 * different number of iterations.
 *
 * @author Francesco Illuminati
 */
public class PerformanceSample implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Map<String, IterationTimeAccumulator> timeMap;

    public PerformanceSample() {
        this.timeMap = new LinkedHashMap<>();
    }

    public PerformanceSample add(final String name,
            final long elapsed, final long iterations) {
        IterationTimeAccumulator time = timeMap.get(name);
        if (time == null) {
            time = new IterationTimeAccumulator();
            timeMap.put(name, time);
        }
        time.add(elapsed, iterations);
        return this;
    }

    public long calculateTotalTime() {
        long total = 0;
        for (IterationTimeAccumulator ti : timeMap.values()) {
            total += ti.getTime();
        }
        return total;
    }

    public Map<String, IterationTime> getTimeMap() {
        return Collections.unmodifiableMap(
                (Map<String, ? extends IterationTime>)timeMap);
    }

    @Override
    public String toString() {
        return StringTableSampleFormatter.INSTANCE.toString(this);
    }
}
