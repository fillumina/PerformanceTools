package com.fillumina.performance.stats;

import com.fillumina.performance.sample.IterationTime;
import com.fillumina.performance.sample.PerformanceSample;
import com.fillumina.performance.util.filter.FilterChain;
import com.fillumina.performance.util.filter.JavaOptimizerFilter;
import com.fillumina.performance.util.filter.ListFilter;
import com.fillumina.performance.util.filter.OutlierEliminatorFilter;
import com.fillumina.performance.util.filter.ValueExtractor;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.MeasureRatio;
import com.fillumina.performance.util.stats.MultipleMeasure;
import com.fillumina.performance.util.stats.OnlineMeasure;
import java.util.ArrayList;
import java.util.Collection;
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
        OnlineMeasure global = new OnlineMeasure();
        LinkedValueHashMap<String, TestPerformance> testPerformanceMap =
                new LinkedValueHashMap<>(timeMap.size());

        for (Map.Entry<String, List<IterationTime>> entry :
                timeMap.entrySet()) {
            String name = entry.getKey();
            List<IterationTime> samples = entry.getValue();

            List<IterationTime> filteredSamples =
                    filterIf(eliminateOutliers, samples);

            testPerformanceMap.put(name,
                    createTestPerformance(name, samples.size(), filteredSamples));

            for (IterationTime ti : filteredSamples) {
                global.add(ti.getTimePerIteration());
            }
        }

        final List<TestPerformance> tpCollection = testPerformanceMap.list();
        MultipleMeasure multiMeasure = createMultiMeasure(tpCollection, global);
        updateTestPerformanceWithPercentageRatio(tpCollection, multiMeasure, confidence);

        return new PerformanceStats(message,
                global,
                multiMeasure,
                testPerformanceMap.map(),
                confidence);
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

    private static TestPerformanceImpl createTestPerformance(
            String name,
            int totalSamples,
            List<IterationTime> sampleList) {
        long iterations = 0;
        long totalTime = 0;
        OnlineMeasure timeMeasure = new OnlineMeasure();
        for (IterationTime it : sampleList) {
            iterations += it.getIterations();
            totalTime += it.getTime();
            timeMeasure.add(it.getTimePerIteration());
        }
        return new TestPerformanceImpl(name,
                timeMeasure,
                iterations,
                totalSamples,
                totalTime);
    }

    private static void updateTestPerformanceWithPercentageRatio(
            final List<TestPerformance> tpCollection,
            final MultipleMeasure multiMeasure,
            final double confidence) {

        int slowIdx = getSlowerIndex(tpCollection);
        Measure slower = tpCollection.get(slowIdx)
                            .getElapsedNanosecondsPerCycle();

        int index = 0;
        for (TestPerformance tp : tpCollection) {
            double tukey;
            if (index == slowIdx) {
                tukey = 1.0;
            } else {
                try {
                    tukey = multiMeasure.tukeyKramerHsdPValue(index, slowIdx);
                } catch (IllegalArgumentException e) {
                    tukey = 1.0; // can't calculate it (not enough data)
                }
            }
            final Measure time = tp.getElapsedNanosecondsPerCycle();
            MeasureRatio ratio = (slower == null) ?
                    new MeasureRatio(time, confidence) :
                    new MeasureRatio(time, slower, confidence);
            ((TestPerformanceImpl)tp).setRatio(ratio, tukey);
            index++;
        }
    }

    private static int getSlowerIndex(Collection<TestPerformance> measures) {
        double mean, slower = Double.NEGATIVE_INFINITY;
        int index = 0, slowerIndex = -1;
        for (TestPerformance tp : measures) {
            mean = tp.getElapsedNanosecondsPerCycle().getMean();
            if (mean > slower) {
                slower = mean;
                slowerIndex = index;
            }
            index++;
        }
        return slowerIndex;
    }

    private static MultipleMeasure createMultiMeasure(List<TestPerformance> list,
            OnlineMeasure global) {
        Measure[] measures = new Measure[list.size()];
        int index = 0;
        for (TestPerformance tp : list) {
            measures[index] = tp.getElapsedNanosecondsPerCycle();
            index++;
        }
        return new MultipleMeasure(global, measures);
    }

    // why LinkedHashMap.values() isn't a List??
    private static class LinkedValueHashMap<K,V> {
        private final  Map<K,V> map;
        private final List<V> list;

        public LinkedValueHashMap(int size) {
            map = new LinkedHashMap<>(size);
            list = new ArrayList<>(size);
        }

        void put(K k, V v) {
            map.put(k, v);
            list.add(v);
        }

        List<V> list() {
            return list;
        }

        Map<K,V> map() {
            return map;
        }
    }
}
