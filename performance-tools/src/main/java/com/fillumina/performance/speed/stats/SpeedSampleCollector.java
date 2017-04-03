package com.fillumina.performance.speed.stats;

import com.fillumina.performance.speed.sample.IterationTime;
import com.fillumina.performance.speed.sample.SpeedSample;
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
 * It uses (default) filters to remove outliers.
 */
public class SpeedSampleCollector {

    private final Map<String, List<IterationTime>> timeMap =
            new LinkedHashMap<>();
    private final ListFilter<IterationTime, Double> sampleFilter;

    /** Use default configuration. */
    @SuppressWarnings("unchecked")
    public SpeedSampleCollector() {
        this(new FilterChain<>(33,
                JavaOptimizerFilter.<IterationTime>instance(),
                OutlierEliminatorFilter.<IterationTime>instance()));
    }

    /**
     * A constructor that allows to define alternative custom filters.
     *
     * @param filter sample filter
     */
    public SpeedSampleCollector(ListFilter<IterationTime, Double> filter) {
        this.sampleFilter = filter;
    }

    /** Adds a sample to the statistics. */
    public void add(final SpeedSample performanceSample) {
        String name;
        IterationTime iterationTime;
        for (Map.Entry<String, IterationTime> entry :
                performanceSample.getTimeMap().entrySet()) {
            name = entry.getKey();
            iterationTime = entry.getValue();
            List<IterationTime> list = getMeasure(name);
            if (list != null) {
                list.add(iterationTime);
            }
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
     *
     * @param applyFilters apply filters (default: outliers elimination)
     * @return the statistics
     */
    public SpeedStats createPerformanceStatsAndFilterIf(boolean applyFilters) {
        SpeedStatsBuilder builder = new SpeedStatsBuilder(timeMap.size());

        for (Map.Entry<String, List<IterationTime>> entry :
                timeMap.entrySet()) {
            String name = entry.getKey();
            List<IterationTime> samples = entry.getValue();
            int originalSize = samples.size();

            List<IterationTime> filteredSamples = filterIf(applyFilters, samples);

            builder.add(name, originalSize, filteredSamples);
        }

        return builder.build();
    }

    private static final ValueExtractor<IterationTime,Double> EXTRACTOR =
            new ValueExtractor<IterationTime,Double>() {
                @Override
                public Double getValue(IterationTime t) {
                    return t.getTimePerIterationNs();
                }
            };

    private List<IterationTime> filterIf(boolean applyFilters,
            List<IterationTime> sampleList) {
        if (applyFilters && sampleFilter != null) {
            return sampleFilter.filter(sampleList, EXTRACTOR);
        } else {
            return sampleList;
        }
    }
}
