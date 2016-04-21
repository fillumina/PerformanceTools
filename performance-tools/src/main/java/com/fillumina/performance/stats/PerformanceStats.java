package com.fillumina.performance.stats;

import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.MultipleMeasure;
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

    public PerformanceStats(IterationRunningMeasure single) {
        totalTime = single.sum();
        testPerformance = createMap(Collections.singletonList(single));
        multiMeasure = new MultipleMeasure(single, new Measure[]{single});
    }

    public PerformanceStats(Measure global,
            List<IterationRunningMeasure> measures) {
        totalTime = global.sum();
        multiMeasure = new MultipleMeasure(global,
                measures.toArray(new Measure[measures.size()]));
        testPerformance = createMap(measures);
    }

    public Map<String, TestPerformances> getTestPerformances() {
        return testPerformance;
    }

    public double getConfidence() {
        return 1 - multiMeasure.anovaPValue();
    }

    public double getTotalTime() {
        return totalTime;
    }

    private Map<String, TestPerformances> createMap(
            final List<IterationRunningMeasure> measures) {
        if (measures.isEmpty()) {
            return Collections.<String, TestPerformances>emptyMap();
        }
        final Map<String, TestPerformances> localMap =
                new LinkedHashMap<>(measures.size());
        final int slowIdx = getSlowerIndex(measures);
        Measure slower = measures.get(slowIdx);
        int index = 0;
        for (IterationRunningMeasure measure : measures) {
            final String name = measure.getName();
            TestPerformances tp = new TestPerformances(name, measure, slower,
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
}
