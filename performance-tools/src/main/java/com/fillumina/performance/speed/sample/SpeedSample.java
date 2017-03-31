package com.fillumina.performance.speed.sample;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.speed.sample.strgen.SampleTableStringGenerator;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.MeasureRatio;
import com.fillumina.performance.util.stats.Ratio;
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
public class SpeedSample implements Assertable, Serializable {
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
    public MeasureRatio getRatioWithSlowestTest(String testName,
            Ratio confidence) {
        double slowestTime = 0;
        double slowest = -1;
        double required = -1;
        for (Map.Entry<String, IterationTime> entry : timeMap.entrySet()) {
            String name = entry.getKey();
            IterationTime iterationTime = entry.getValue();
            double timeNs = iterationTime.getTimePerIterationNs();
            if (timeNs > slowestTime) {
                slowestTime = timeNs;
                slowest = timeNs;
            }
            if (name.equals(testName)) {
                required = timeNs;
            }
        }
        if (slowest == -1) {
            throw new AssertionError("test time is 0");
        }
        if (required == -1) {
            throw new IllegalArgumentException("experiment '" + testName +
                    "' not found");
        }
        Measure requiredMeasure = new DimensionalOnlineMeasure(
                IntervalUnit.NANOSECONDS, required);
        Measure slowestMeasure = new DimensionalOnlineMeasure(
                IntervalUnit.NANOSECONDS, slowest);
        return new MeasureRatio(requiredMeasure, slowestMeasure, confidence);
    }

    @Override
    public String toString() {
        return SampleTableStringGenerator.INSTANCE.toString(this);
    }
}
