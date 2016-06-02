package com.fillumina.performance.stats;

import com.fillumina.performance.sample.IterationTime;
import com.fillumina.performance.sample.PerformanceSample;
import com.fillumina.performance.util.filter.FilterChain;
import com.fillumina.performance.util.filter.JavaOptimizerFilter;
import com.fillumina.performance.util.filter.ListFilter;
import com.fillumina.performance.util.filter.OutlierEliminatorFilter;
import com.fillumina.performance.util.filter.ValueExtractor;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Collects samples and creates statistics out of them.
 * It uses filters to remove outliers.
 */
public class PerformanceSampleCollector {

    private final Map<String, List<IterationTime>> timeMap =
            new LinkedHashMap<>();
    private final double confidence;
    private final ListFilter<IterationTime, Double> sampleFilter;

    /** Use default configuration. */
    public PerformanceSampleCollector() {
        this(0.95);
    }

    /**
     * @param confidence with which statistics are reported
     */
    public PerformanceSampleCollector(double confidence) {
        this(confidence, new FilterChain<>(33,
                JavaOptimizerFilter.<IterationTime>instance(),
                OutlierEliminatorFilter.<IterationTime>instance()));
    }

    /**
     *
     * @param confidence with which statistics are reported
     * @param filter outliers
     */
    public PerformanceSampleCollector(double confidence,
            ListFilter<IterationTime, Double> filter) {
        this.confidence = confidence;
        this.sampleFilter = filter;
    }

    /** Adds a sample to the statistics. */
    public void add(final PerformanceSample performanceSample) {
        String name;
        IterationTime ti;
        for (Map.Entry<String, IterationTime> entry :
                performanceSample.getTimeMap().entrySet()) {
            name = entry.getKey();
            ti = entry.getValue();
            getMeasure(name).add(ti);
        }
    }

    private List<IterationTime> getMeasure(String name) {
        List<IterationTime> list = timeMap.get(name);
        if (list == null) {
            list = new ArrayList<>(100);
            timeMap.put(name, list);
        }
        return list;
    }

    /**
     * Passes a copy of the internal data so sample collection can continue.
     */
    public PerformanceStats createPerformanceStats(String message,
            boolean eliminateOutliers) {
        PerformanceStatsBuilder builder =
                new PerformanceStatsBuilder(timeMap.size());

        for (Map.Entry<String, List<IterationTime>> entry :
                timeMap.entrySet()) {
            String name = entry.getKey();
            List<IterationTime> samples = entry.getValue();

            List<IterationTime> filteredSamples =
                    filterIf(eliminateOutliers, samples);

            builder.add(name, samples.size(), filteredSamples);
        }

        return builder.createPerformanceStats(message, confidence);
    }

    private static final ValueExtractor<IterationTime,Double> EXTRACTOR =
            new ValueExtractor<IterationTime,Double>() {
                @Override
                public Double getValue(IterationTime t) {
                    return t.getTimePerIteration();
                }
            };

    private List<IterationTime> filterIf(boolean eliminateOutliers,
            List<IterationTime> sampleList) {
        if (eliminateOutliers && sampleFilter != null && sampleList.size() > 30) {
            return sampleFilter.filter(sampleList, EXTRACTOR);
        } else {
            return sampleList;
        }
    }

}
