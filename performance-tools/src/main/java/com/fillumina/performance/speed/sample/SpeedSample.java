package com.fillumina.performance.speed.sample;

import com.fillumina.performance.assertion.AssertableMultiStats;
import com.fillumina.performance.speed.sample.strgen.SampleTableStringGenerator;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.MeasureRatio;
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
public class SpeedSample implements AssertableMultiStats, Serializable {
    private static final long serialVersionUID = 1L;

    private final long totalTime;
    private final Map<String, IterationTime> timeMap;

    public SpeedSample(long totalTime,
            Map<String, IterationTime> timeMap) {
        this.totalTime = totalTime;
        this.timeMap = timeMap;
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
    public Measure getValue(String testName) {
        IterationTime iterationTime = timeMap.get(testName);
        if (iterationTime == null) {
            return null;
        }
        double timeNs = iterationTime.getTimePerIterationNs();
        return new DimensionalOnlineMeasure(IntervalUnit.NANOSECONDS, timeNs);
    }

    @Override
    public MeasureRatio getRatioWithSlowestTest(String testName) {
        Map<String, Measure> measureMap = new HashMap<>(timeMap.size());
        double slowestTime = 0;
        Measure slowest = null;
        Measure required = null;
        for (Map.Entry<String, IterationTime> entry : timeMap.entrySet()) {
            String name = entry.getKey();
            IterationTime iterationTime = entry.getValue();
            double timeNs = iterationTime.getTimePerIterationNs();
            Measure m = new DimensionalOnlineMeasure(
                    IntervalUnit.NANOSECONDS, timeNs);
            if (timeNs > slowestTime) {
                slowestTime = timeNs;
                slowest = m;
            }
            if (name.equals(testName)) {
                required = m;
            }
            measureMap.put(name, m);
        }
        return new MeasureRatio(required, slowest, 0.99);
    }

    @Override
    public String toString() {
        return SampleTableStringGenerator.INSTANCE.toString(this);
    }
}
