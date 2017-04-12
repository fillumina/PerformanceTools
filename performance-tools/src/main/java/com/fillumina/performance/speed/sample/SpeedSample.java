package com.fillumina.performance.speed.sample;

import com.fillumina.performance.assertion.AbstractAssertable;
import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.speed.sample.strgen.SampleTableStringGenerator;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.unit.DimensionalOnlineMeasure;
import com.fillumina.performance.util.unit.IntervalUnit;
import java.io.Serializable;
import java.util.*;

/**
 * Keeps the test elapsed times. Each test might have been executed for a
 * different number of iterations.
 * This class is immutable.
 *
 * @author Francesco Illuminati
 */
public class SpeedSample extends AbstractAssertable
        implements Assertable, Serializable {
    private static final long serialVersionUID = 1L;

    private final long totalTime;
    private final Map<String, IterationTime> timeMap;

    public SpeedSample(Map<String, IterationTime> timeMap) {
        this.totalTime = calculateTotalTime(timeMap);
        this.timeMap = Collections.unmodifiableMap(new LinkedHashMap<>(timeMap));
    }

    /**
     * @return the time spent in all the iterations of all the tests in
     * the sample (nanoseconds).
     */
    public long getTotalTimeNs() {
        return totalTime;
    }

    public Map<String, IterationTime> getTimeMap() {
        return timeMap;
    }

    @Override
    public Collection<String> getTestNames() {
        return timeMap.keySet();
    }

    @Override
    public Measure getMeasure(String testName) {
        IterationTime iterationTime = timeMap.get(testName);
        if (iterationTime == null) {
            return null;
        }
        double timeNs = iterationTime.getTimePerIterationNs();
        return new DimensionalOnlineMeasure(IntervalUnit.NANOSECONDS, timeNs);
    }

    private long calculateTotalTime(Map<String, IterationTime> timeMap) {
        long total = 0;
        for (IterationTime it : timeMap.values()) {
            total += it.getTimeNs();
        }
        return total;
    }

    @Override
    public String toString() {
        return SampleTableStringGenerator.INSTANCE.toString(this);
    }
}
