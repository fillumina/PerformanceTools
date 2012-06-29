package com.fillumina.performance;

import java.io.Serializable;
import java.util.*;

/**
 *
 * @author fra
 */
public class Performance implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Map<String, Long> timeMap = new LinkedHashMap<>();
    private final Map<String, Long> unmodifiableMap =
            Collections.unmodifiableMap(timeMap);

    private long iterations;

    public void incrementIterationsBy(final int value) {
        iterations += value;
    }

    public long getIterations() {
        return iterations;
    }

    public void add(final String msg, final long elapsed) {
        final Long time = timeMap.get(msg);
        final long value;
        if (time == null) {
            value = elapsed;
        } else {
            value = time + elapsed;
        }
        timeMap.put(msg, value);
    }

    public Map<String, Long> getTimeMap() {
        return unmodifiableMap;
    }

    public UnmodifiableStatistics getStatistics() {
        final Collection<Long> values = timeMap.values();
        return new UnmodifiableStatistics(new Statistics(values));
    }

    public Collection<Double> getPercentages() {
        return getPercentageMap().values();
    }

    public Map<String, Double> getPercentageMap() {
        final Map<String, Double> map = new LinkedHashMap<>();
        final UnmodifiableStatistics stats = getStatistics();

        final long slowest = Math.round(stats.max());
        for (final Map.Entry<String, Long> entry: timeMap.entrySet()) {
            final String msg = entry.getKey();
            final long value = entry.getValue();

            final double percentageValue = value * 1.0D / slowest;
            map.put(msg, percentageValue);
        }

        return map;
    }

    public void clear() {
        timeMap.clear();
        iterations = 0;
    }

}
