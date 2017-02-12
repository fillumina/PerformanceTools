package com.fillumina.performance.speed.stats;

import com.fillumina.performance.speed.sample.IterationTime;
import com.fillumina.performance.util.Builder;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.MeasureRatio;
import com.fillumina.performance.util.stats.MultipleMeasure;
import com.fillumina.performance.util.stats.OnlineMeasure;
import com.fillumina.performance.util.unit.DimensionalOnlineMeasure;
import com.fillumina.performance.util.unit.IntervalUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Builds a {@link SpeedStats} out of collected samples.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
class SpeedStatsBuilder implements Builder<SpeedStats> {

    private final Map<String, TestPerformance> map;
    private final List<TestPerformance> list;
    private final OnlineMeasure global = new OnlineMeasure();
    private final double confidence;

    public SpeedStatsBuilder(double confidence, int size) {
        this.map = new LinkedHashMap<>(size);
        this.list = new ArrayList<>(size);
        this.confidence = confidence;
    }

    /**
     * Adds the samples relative to the named test. Because the samples could
     * have been filtered to eliminate outliers it records the original samples
     * number too.
     *
     * @param name          test's name
     * @param totalSamples  total number of samples collects (including filtered
     *                      ones)
     * @param samples       samples
     */
    public void add(String name, int totalSamples, List<IterationTime> samples) {
        long totalIterations = 0;
        long totalTime = 0;
        DimensionalOnlineMeasure timeMeasure =
                new DimensionalOnlineMeasure(IntervalUnit.NANOSECONDS);

        for (IterationTime it : samples) {
            totalIterations += it.getIterations();
            totalTime += it.getTimeNs();

            final double timePerIteration = it.getTimePerIterationNs();
            timeMeasure.add(timePerIteration);
            global.add(timePerIteration);
        }

        put(name, new TestPerformance(name, timeMeasure, totalIterations,
                                      samples.size(), totalSamples, totalTime));
    }

    /**
     * Builds a {@link SpeedStats} out of the collected samples.
     *
     * @param message       The message to add to the statistics
     * @param confidence    The confidence used
     * @return              The statistics computed over the collected samples
     */
    @Override
    public SpeedStats build() {
        MultipleMeasure multiMeasure = createMultiMeasure(global, list);
        updateTestPerformanceWithPercentageRatio(confidence, multiMeasure, list);
        return new SpeedStats(global, multiMeasure, map);
    }

    private void put(String k, TestPerformance v) {
        map.put(k, v);
        list.add(v);
    }

    static void updateTestPerformanceWithPercentageRatio(
            final double confidence,
            final MultipleMeasure multiMeasure,
            final List<TestPerformance> list) {
        int slowIdx = getSlowerIndex(list);
        if (slowIdx != -1) {
            Measure slower = list.get(slowIdx).getElapsedNanosecondsPerCycle();
            int index = 0;
            for (TestPerformance tp : list) {
                MeasureRatio ratio = createRatio(tp, slower, confidence);
                double tukey = calculateTukey(index, slowIdx, multiMeasure);

                tp.setRatio(ratio, tukey);

                index++;
            }
        }
    }

    static double calculateTukey(int index, int slowIdx,
            final MultipleMeasure multiMeasure) {
        double tukey;
        if (index == slowIdx) {
            // tukey with itself is always true
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

    static MeasureRatio createRatio(TestPerformance tp,
            Measure slower,
            final double confidence) {
        final Measure time = tp.getElapsedNanosecondsPerCycle();
        if (slower == null) {
            return new MeasureRatio(time, confidence);
        }
        return new MeasureRatio(time, slower, confidence);
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
        Measure[] measures = extractMeasureArray(list);
        return new MultipleMeasure(global, measures);
    }

    static Measure[] extractMeasureArray(List<TestPerformance> list) {
        Measure[] measures = new Measure[list.size()];
        int index = 0;
        for (TestPerformance tp : list) {
            measures[index] = tp.getElapsedNanosecondsPerCycle();
            index++;
        }
        return measures;
    }

}
