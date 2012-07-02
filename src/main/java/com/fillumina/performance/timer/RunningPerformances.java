package com.fillumina.performance.timer;

import com.fillumina.performance.timer.LoopPerformances;
import com.fillumina.performance.utils.Statistics;
import com.fillumina.performance.utils.Statistics;
import java.io.Serializable;
import java.util.*;

/**
 *
 * @author fra
 */
public class RunningPerformances implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Map<String, Long> timeMap = new LinkedHashMap<>();
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

    public LoopPerformances getLoopPerformances() {
        return new LoopPerformances(iterations, getStatistics(), timeMap);
    }

    private Statistics getStatistics() {
        final Collection<Long> values = timeMap.values();
        return new Statistics(values);
    }

    public void clear() {
        timeMap.clear();
        iterations = 0;
    }

}
