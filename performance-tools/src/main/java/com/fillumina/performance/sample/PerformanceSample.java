package com.fillumina.performance.sample;

import com.fillumina.performance.sample.formatter.StringTableSampleFormatter;
import java.io.Serializable;
import java.util.*;

/**
 * Keeps the test elapsed times. Each test might have been executed for a
 * different number of iterations.
 * This class is immutable.
 *
 * @author Francesco Illuminati
 */
public class PerformanceSample implements Serializable {
    private static final long serialVersionUID = 1L;

    private final long totalTime;
    private final Map<String, IterationTime> timeMap;

    public PerformanceSample(long totalTime,
            Map<String, IterationTime> timeMap) {
        this.totalTime = totalTime;
        this.timeMap = timeMap;
    }

    public long getTotalTime() {
        return totalTime;
    }

    public Map<String, IterationTime> getTimeMap() {
        return timeMap;
    }

    @Override
    public String toString() {
        return StringTableSampleFormatter.INSTANCE.toString(this);
    }
}
