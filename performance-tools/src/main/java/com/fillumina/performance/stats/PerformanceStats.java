package com.fillumina.performance.stats;

import com.fillumina.performance.stats.viewer.StringTableStatsViewer;
import com.fillumina.performance.util.stats.MultipleMeasure;
import com.fillumina.performance.util.stats.OnlineMeasure;
import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * It reads {@link PerformanceSample} and calculates
 * average performances and maximum standard deviation.
 *
 * @author Francesco Illuminati
 */
public class PerformanceStats implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final PerformanceStats EMPTY = new PerformanceStats();

    private final Map<String, TestPerformances> testPerformance;
    private final MultipleMeasure multiMeasure;
    private final double totalTime;

    /** private empty constructor */
    private PerformanceStats() {
        testPerformance = Collections.<String,TestPerformances>emptyMap();
        multiMeasure = MultipleMeasure.EMPTY;
        totalTime = 0;
    }

    public PerformanceStats(IterationRunningMeasure single,
            final double confidence) {
        totalTime = single.sum();
        testPerformance = createMap(Collections.singletonList(single),
                confidence);
        multiMeasure = new MultipleMeasure(single, new OnlineMeasure[]{single});
    }

    public PerformanceStats(OnlineMeasure global,
            List<IterationRunningMeasure> measures,
            double confidence) {
        totalTime = global.sum();
        multiMeasure = new MultipleMeasure(global,
                measures.toArray(new OnlineMeasure[measures.size()]));
        testPerformance = createMap(measures, confidence);
    }

    public Map<String, TestPerformances> getTestPerformances() {
        return testPerformance;
    }

    public double getConfidence() {
        if (multiMeasure.anovaPValue() == 0.0) {
            // if some of the variances is 0, ANOVA is 0
            return 1.0;
        }
        return 1.0 - getMaxTukeyHsd();
    }

    public double getTotalTime() {
        return totalTime;
    }

    public double getAnova() {
        return multiMeasure.anovaPValue();
    }

    public double getMaxTukeyHsd() {
        double max = Double.NEGATIVE_INFINITY;
        int count = multiMeasure.getMeasureCount();
        for (int i=0; i<count; i++) {
            for (int j=i+1; j<count; j++) {
                double tukey = multiMeasure.tukeyKramerHsdPValue(i, j);
                if (max < tukey) {
                    max = tukey;
                }
            }
        }
        return max;
    }

    private Map<String, TestPerformances> createMap(
            final List<IterationRunningMeasure> measures,
            final double confidence) {
        if (measures.isEmpty()) {
            return Collections.<String, TestPerformances>emptyMap();
        }
        final Map<String, TestPerformances> localMap =
                new LinkedHashMap<>(measures.size());
        final int slowIdx = getSlowerIndex(measures);
        OnlineMeasure slower = measures.get(slowIdx);
        int index = 0;
        for (IterationRunningMeasure measure : measures) {
            final String name = measure.getName();
            TestPerformances tp = new TestPerformances(
                    name,
                    measure,
                    slower,
                    confidence,
                    tukey(index, slowIdx),
                    measure.getIterations(),
                    measure.getTotalTime());
            localMap.put(measure.getName(), tp);
            index++;
        }
        return Collections.unmodifiableMap(localMap);
    }

    private double tukey(int index, final int slowIdx) {
        if (index == slowIdx) {
            return 1.0;
        }
        return multiMeasure.tukeyKramerHsdPValue(index, slowIdx);
    }

    private int getSlowerIndex(List<IterationRunningMeasure> measures) {
        double mean, slower = Double.NEGATIVE_INFINITY;
        int index = 0, slowerIndex = -1;
        for (IterationRunningMeasure m : measures) {
            mean = m.mean();
            if (mean > slower) {
                slower = mean;
                slowerIndex = index;
            }
            index++;
        }
        return slowerIndex;
    }

    @Override
    public String toString() {
        return StringTableStatsViewer.getTable(null, this).toString();
    }
}
