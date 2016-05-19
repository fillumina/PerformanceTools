package com.fillumina.performance.stats;

import com.fillumina.performance.sample.PerformanceSample;
import com.fillumina.performance.sample.TimeIteration;
import com.fillumina.performance.util.filter.JavaOptimizerFilter;
import com.fillumina.performance.util.filter.OutlierEliminatorFilter;
import com.fillumina.performance.util.filter.SampleFilter;
import com.fillumina.performance.util.filter.SampleFilterChain;
import com.fillumina.performance.util.filter.ValueExtractor;
import com.fillumina.performance.util.stats.OnlineMeasure;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 */
public class PerformanceDataCollector {

    private final Map<String, List<TimeIteration>> timeMap = new LinkedHashMap<>();
    private final double confidence;
    private final SampleFilter sampleFilter;

    public PerformanceDataCollector() {
        this(0.95);
    }

    public PerformanceDataCollector(double confidence) {
        this(confidence, new SampleFilterChain(
                JavaOptimizerFilter.INSTANCE,
                OutlierEliminatorFilter.INSTANCE));
    }

    public PerformanceDataCollector(double confidence, SampleFilter filter) {
        this.confidence = confidence;
        this.sampleFilter = filter;
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

    private static final ValueExtractor<TimeIteration,Double> EXTRACTOR =
            new ValueExtractor<TimeIteration,Double>() {
                @Override
                public Double getValue(TimeIteration t) {
                    return t.getTimePerIteration();
                }
            };

    /** Passes a copy of the internal data so collection can be continued. */
    public PerformanceStats createPerformanceStats(String message,
            boolean eliminateOutliers) {
        OnlineMeasure global = new OnlineMeasure();
        List<IterationRunningMeasure> irmList = new ArrayList<>(timeMap.size());
        for (Map.Entry<String, List<TimeIteration>> entry : timeMap.entrySet()) {
            String name = entry.getKey();
            List<TimeIteration> sampleList = entry.getValue();
            List<TimeIteration> cleaned;
            int sampleBeforeCleaning = sampleList.size();
            if (eliminateOutliers && sampleList.size() > 5) {
                cleaned = sampleFilter.filter(sampleList, EXTRACTOR);
            } else {
                cleaned = sampleList;
            }
            IterationRunningMeasure irm =
                    new IterationRunningMeasure(name, sampleBeforeCleaning);
            for (TimeIteration ti : cleaned) {
                irm.add(ti);
                global.add(ti.getTimePerIteration());
            }
            irmList.add(irm);
        }
        return new PerformanceStats(message,
                new OnlineMeasure(global),
                irmList,
                confidence);
    }
}
