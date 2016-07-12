package com.fillumina.performance.speed.sample;

import com.fillumina.performance.speed.sample.strgen.SampleTableStringGenerator;
import java.io.Serializable;
import java.util.*;

/**
 * Keeps the test elapsed times. Each test might have been executed for a
 * different number of iterations.
 * This class is immutable.
 *
 * @author Francesco Illuminati
 */
public class SpeedSample implements Serializable {
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
    public long getTotalTime() {
        return totalTime;
    }

    public Map<String, IterationTime> getTimeMap() {
        return timeMap;
    }

    @Override
    public String toString() {
        return SampleTableStringGenerator.INSTANCE.toString(this);
    }
}
