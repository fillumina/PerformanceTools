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
 */
public class PerformanceDataCollector {

    private final Map<String, List<IterationTime>> timeMap =
            new LinkedHashMap<>();
    private final double confidence;
    private final ListFilter<IterationTime, Double> sampleFilter;

    public PerformanceDataCollector() {
        this(0.95);
    }

    public PerformanceDataCollector(double confidence) {
        // TODO should filters be here?
        this(confidence, new FilterChain<>(33,
                JavaOptimizerFilter.<IterationTime>instance(),
                OutlierEliminatorFilter.<IterationTime>instance()));
    }

    public PerformanceDataCollector(double confidence,
            ListFilter<IterationTime, Double> filter) {
        this.confidence = confidence;
        this.sampleFilter = filter;
    }

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

    private static final ValueExtractor<IterationTime,Double> EXTRACTOR =
            new ValueExtractor<IterationTime,Double>() {
                @Override
                public Double getValue(IterationTime t) {
                    return t.getTimePerIteration();
                }
            };

    /** Passes a copy of the internal data so collection can be continued. */
    public PerformanceStats createPerformanceStats(String message,
            boolean eliminateOutliers) {
        OnlineMeasure global = new OnlineMeasure();
        LinkedValueHashMap<String, TestPerformance> testPerformances =
                new LinkedValueHashMap<>(timeMap.size());
        for (Map.Entry<String, List<IterationTime>> entry :
                timeMap.entrySet()) {
            String name = entry.getKey();
            List<IterationTime> sampleList = entry.getValue();

            int totalCollectedSamplesNumber = sampleList.size();

            List<IterationTime> filteredSampleList =
                    filter(eliminateOutliers, sampleList);

            TestPerformanceImpl testPerformance = createTestPerformance(name,
                    totalCollectedSamplesNumber, sampleList);

            testPerformances.put(name, testPerformance);

            for (IterationTime ti : filteredSampleList) {
                global.add(ti.getTimePerIteration());
            }
        }
        final List<TestPerformance> tpCollection =
                testPerformances.list();

        int slowIdx = getSlowerIndex(tpCollection);
        Measure slower = tpCollection.get(slowIdx)
                .getElapsedNanosecondsPerCycle();

        MultipleMeasure multiMeasure = createMultipleMeasure(
                global, testPerformances.list());

        int index = 0;
        for (TestPerformance tp : testPerformances.list()) {
            updateTestPerformance((TestPerformanceImpl)tp,
                    index, slowIdx, slower, multiMeasure);
            index++;
        }

        return new PerformanceStats(message,
                global,
                multiMeasure,
                testPerformances.map(),
                confidence);
    }

    private List<IterationTime> filter(boolean eliminateOutliers,
            List<IterationTime> sampleList) {
        if (eliminateOutliers && sampleList.size() > 5) {
            return sampleFilter.filter(sampleList, EXTRACTOR);
        } else {
            return sampleList;
        }
    }

    private TestPerformanceImpl createTestPerformance(
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
                confidence,
                iterations,
                totalSamples,
                totalTime);
    }

    private void updateTestPerformance(
            final TestPerformanceImpl tp,
            final int index,
            final int slowIdx,
            final Measure slower,
            final MultipleMeasure multiMeasure) {
        double tukey;
        if (index == slowIdx) {
            tukey = 1.0;
        } else {
            try {
                tukey = multiMeasure.tukeyKramerHsdPValue(index, slowIdx);
            } catch (IllegalArgumentException e) {
                tukey = 1.0; // can't calculate it (too few data)
            }
        }
        final Measure time = tp.getElapsedNanosecondsPerCycle();
        MeasureRatio ratio = (slower == null) ?
                new MeasureRatio(time, confidence) :
                new MeasureRatio(time, slower, confidence);
        tp.setRatio(ratio, tukey);
    }

    private int getSlowerIndex(Collection<TestPerformance> measures) {
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

    private MultipleMeasure createMultipleMeasure(OnlineMeasure global,
            List<TestPerformance> list) {
        Measure[] measures = new Measure[list.size()];
        int index = 0;
        for (TestPerformance tp : list) {
            measures[index] = tp.getElapsedNanosecondsPerCycle();
            index++;
        }
        return new MultipleMeasure(global, measures);
    }

    // why LinkedHashMap.values() doesn't return a List??
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
