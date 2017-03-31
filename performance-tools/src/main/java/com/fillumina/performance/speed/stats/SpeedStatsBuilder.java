package com.fillumina.performance.speed.stats;

import com.fillumina.performance.speed.sample.IterationTime;
import com.fillumina.performance.util.Builder;
import com.fillumina.performance.util.stats.Measure;
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

    private final Map<String, TestStats> map;
    private final List<TestStats> list;
    private final OnlineMeasure global = new OnlineMeasure();

    public SpeedStatsBuilder(int size) {
        this.map = new LinkedHashMap<>(size);
        this.list = new ArrayList<>(size);
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

        put(name, new TestStats(name, timeMeasure, totalIterations,
                                      samples.size(), totalSamples, totalTime));
    }

    private void put(String k, TestStats v) {
        map.put(k, v);
        list.add(v);
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
        return new SpeedStats(global, multiMeasure, map);
    }

    static MultipleMeasure createMultiMeasure(Measure global,
            List<TestStats> list) {
        Measure[] measures = extractMeasureArray(list);
        return new MultipleMeasure(global, measures);
    }

    static Measure[] extractMeasureArray(List<TestStats> list) {
        Measure[] measures = new Measure[list.size()];
        int index = 0;
        for (TestStats tp : list) {
            measures[index] = tp.getElapsedNanosecondsPerCycle();
            index++;
        }
        return measures;
    }

}
