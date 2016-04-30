package com.fillumina.performance.stats;

import com.fillumina.performance.sample.PerformanceSample;
import com.fillumina.performance.sample.TimeIteration;
import com.fillumina.performance.util.stats.OutlierEliminator;
import com.fillumina.performance.util.stats.OutlierEliminator.ValueExtractor;
import com.fillumina.performance.util.stats.RunningOnlineMeasure;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 */
public class PerformanceDataCollector {

    private final Map<String, List<TimeIteration>> timeMap = new LinkedHashMap<>();
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
        }
    }

    private List<TimeIteration> getMeasure(String name) {
        List<TimeIteration> list = timeMap.get(name);
        if (list == null) {
            list = new ArrayList<>(100);
            timeMap.put(name, list);
        }
        return list;
    }

    private static final ValueExtractor<TimeIteration> EXTRACTOR =
            new ValueExtractor<TimeIteration>() {
                @Override
                public double getValue(TimeIteration t) {
                    return t.getTimePerIteration();
                }
            };

    /** Passes a copy of the internal data so collection can be continued. */
    public PerformanceStats createPerformanceStats(boolean eliminateOutliers) {
      RunningOnlineMeasure global = new RunningOnlineMeasure();
      List<IterationRunningMeasure> irmList = new ArrayList<>(timeMap.size());
        for (Map.Entry<String, List<TimeIteration>> entry : timeMap.entrySet()) {
            String name = entry.getKey();
            List<TimeIteration> list = entry.getValue();
            List<TimeIteration> cleaned;
            if (eliminateOutliers) {
                cleaned =
                    OutlierEliminator.eliminateOutliers(list, EXTRACTOR);
            } else {
                cleaned = list;
            }
            IterationRunningMeasure irm = new IterationRunningMeasure(name);
            for (TimeIteration ti : cleaned) {
                irm.add(ti);
                global.add(ti.getTimePerIteration());
            }
            irmList.add(irm);
        }
        return new PerformanceStats(
                new RunningOnlineMeasure(global), irmList, confidence);
    }
}
