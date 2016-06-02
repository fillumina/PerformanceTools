package com.fillumina.performance.stats;

import com.fillumina.performance.sample.IterationTime;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.MeasureRatio;
import com.fillumina.performance.util.stats.MultipleMeasure;
import com.fillumina.performance.util.stats.OnlineMeasure;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
class PerformanceStatsBuilder {

    private final Map<String, TestPerformance> map;
    private final List<TestPerformance> list;
    private final OnlineMeasure global = new OnlineMeasure();

    public PerformanceStatsBuilder(int size) {
        map = new LinkedHashMap<>(size);
        list = new ArrayList<>(size);
    }

    private void put(String k, TestPerformance v) {
        map.put(k, v);
        list.add(v);
    }

    public void add(String name, int totalSamples, List<IterationTime> samples) {
        long iterations = 0;
        long totalTime = 0;
        OnlineMeasure timeMeasure = new OnlineMeasure();

        for (IterationTime it : samples) {
            iterations += it.getIterations();
            totalTime += it.getTime();

            final double timePerIteration = it.getTimePerIteration();
            timeMeasure.add(timePerIteration);
            global.add(timePerIteration);
        }

        put(name, new TestPerformanceImpl(name, timeMeasure, iterations,
                                          totalSamples, totalTime));
    }

    public PerformanceStats createPerformanceStats(final String message,
            final double confidence) {
        MultipleMeasure multiMeasure = createMultiMeasure(global, list);
        updateTestPerformanceWithPercentageRatio(confidence, multiMeasure, list);
        return new PerformanceStats(message,
                global,
                multiMeasure,
                map,
                confidence);
    }

    static void updateTestPerformanceWithPercentageRatio(
            final double confidence,
            final MultipleMeasure multiMeasure,
            final List<TestPerformance> list) {
        int slowIdx = getSlowerIndex(list);
        Measure slower = list.get(slowIdx).getElapsedNanosecondsPerCycle();
        int index = 0;
        for (TestPerformance tp : list) {
            MeasureRatio ratio = createRatio(tp, slower, confidence);
            double tukey = calculateTukey(index, slowIdx, multiMeasure);

            ((TestPerformanceImpl) tp).setRatio(ratio, tukey);

            index++;
        }
    }

    static double calculateTukey(int index, int slowIdx,
            final MultipleMeasure multiMeasure) {
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
        return tukey;
    }

    static MeasureRatio createRatio(TestPerformance tp, Measure slower,
            final double confidence) {
        final Measure time = tp.getElapsedNanosecondsPerCycle();
        MeasureRatio ratio =
                (slower == null) ? new MeasureRatio(time, confidence)
                : new MeasureRatio(time, slower, confidence);
        return ratio;
    }

    static int getSlowerIndex(List<TestPerformance> measures) {
        double mean;
        double slower = Double.NEGATIVE_INFINITY;
        int index = 0;
        int slowerIndex = -1;
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

    static MultipleMeasure createMultiMeasure(Measure global,
            List<TestPerformance> list) {
        Measure[] measures = new Measure[list.size()];
        int index = 0;
        for (TestPerformance tp : list) {
            measures[index] = tp.getElapsedNanosecondsPerCycle();
            index++;
        }
        return new MultipleMeasure(global, measures);
    }

}
