package com.fillumina.performance.stats;

import com.fillumina.performance.sample.PerformanceSample;
import com.fillumina.performance.util.stats.RunningOnlineMeasure;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 */
public class PerformanceDataCollector {

    private final RunningOnlineMeasure global = new RunningOnlineMeasure();
    private final LinkedHashMap<String, IterationRunningMeasure> measureMap =
            new LinkedHashMap<>();
    private final double confidence;

    public PerformanceDataCollector() {
        this(0.95);
    }

    public PerformanceDataCollector(double confidence) {
        this.confidence = confidence;
    }

    public void add(final PerformanceSample performanceSample) {
        String name;
        TimeIteration ti;
        for (Map.Entry<String, TimeIteration> entry :
                performanceSample.getTimeMap().entrySet()) {
            name = entry.getKey();
            ti = entry.getValue();
            getMeasure(name).add(ti);
            global.add(ti.getTimePerIteration());
        }
    }

    private IterationRunningMeasure getMeasure(String name) {
        IterationRunningMeasure m = measureMap.get(name);
        if (m == null) {
            m = new IterationRunningMeasure(name);
            measureMap.put(name, m);
        }
        return m;
    }

    /** Passes a copy of the internal data so collection can be continued. */
    public PerformanceStats createPerformanceStats() {
        List<IterationRunningMeasure> list = new ArrayList<>(measureMap.size());
        for (IterationRunningMeasure m : measureMap.values()) {
            list.add(new IterationRunningMeasure(m));
        }
        return new PerformanceStats(
                new RunningOnlineMeasure(global), list, confidence);
    }
}
