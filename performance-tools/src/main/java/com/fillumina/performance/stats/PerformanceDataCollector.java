package com.fillumina.performance.stats;

import com.fillumina.performance.sample.PerformanceSample;
import com.fillumina.performance.util.stats.RunningMeasure;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 */
public class PerformanceDataCollector {

    private final RunningMeasure global = new RunningMeasure();
    private final LinkedHashMap<String, IterationRunningMeasure> measureMap =
            new LinkedHashMap<>();

    public void add(final PerformanceSample performanceSample) {
        String name;
        TimeIteration it;
        for (Map.Entry<String, TimeIteration> entry :
                performanceSample.getTimeMap().entrySet()) {
            name = entry.getKey();
            it = entry.getValue();
            getMeasure(name).add(it);
            global.add(it.getTimePerIteration());
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
        return new PerformanceStats(new RunningMeasure(global), list);
    }
}
