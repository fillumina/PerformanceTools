package com.fillumina.performance.sample;

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

    public static final PerformanceSample EMPTY = new PerformanceSample(0);

    private final Map<String, TimeIteration> timeMap;

    public PerformanceSample() {
        this.timeMap = new LinkedHashMap<>();
    }

    /** @param unused only used to distinguish this private constructor. */
    private PerformanceSample(Object unused) {
        this.timeMap = Collections.<String, TimeIteration>emptyMap();
    }

    public PerformanceSample add(final String name,
            final long elapsed, final long iterations) {
        TimeIteration time = timeMap.get(name);
        if (time == null) {
            time = new TimeIteration();
            timeMap.put(name, time);
        }
        time.add(elapsed, iterations);
        return this;
    }

    public long calculateTotalTime(final Map<String, TimeIteration> timeMap) {
        long total = 0;
        for (TimeIteration ti : timeMap.values()) {
            total += ti.getTime();
        }
        return total;
    }

    public boolean isEmpty() {
        return timeMap.isEmpty();
    }

    public Map<String, TimeIteration> getTimeMap() {
        return Collections.unmodifiableMap(timeMap);
    }

    @Override
    public String toString() {
        return timeMap.toString();
    }
}
