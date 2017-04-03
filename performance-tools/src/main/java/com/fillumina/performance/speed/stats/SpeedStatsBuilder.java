package com.fillumina.performance.speed.stats;

import com.fillumina.performance.speed.sample.IterationTime;
import com.fillumina.performance.util.Builder;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.MultiMeasure;
import com.fillumina.performance.util.stats.OnlineMeasure;
import com.fillumina.performance.util.unit.DimensionalOnlineMeasure;
import com.fillumina.performance.util.unit.IntervalUnit;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Builds a {@link SpeedStats} out of collected {@link SingleTestStats}.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
class SpeedStatsBuilder implements Builder<SpeedStats> {

    private final LinkedHashMap<String, SingleTestStats> map;
    private final OnlineMeasure global = new OnlineMeasure();

    /**
     *
     * @param testCount the number of tests
     */
    public SpeedStatsBuilder(int testCount) {
        this.map = new LinkedHashMap<>(testCount);
    }

    /**
     * Adds the samples relative to the named test. Because the samples could
     * have been filtered to eliminate outliers it records the original samples
     * number too.
     *
     * @param name          test's name
     * @param originalSamples  total number of samples collects (including filtered
     *                      ones)
     * @param samples       samples
     */
    public void add(String name, int originalSamples,
            List<IterationTime> samples) {
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

        SingleTestStats singleTestStats =
                new SingleTestStats(name, timeMeasure, totalIterations,
                        samples.size(), originalSamples, totalTime);

        map.put(name, singleTestStats);
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
        MultiMeasure multiMeasure = createMultiMeasure(global, map);
        return new SpeedStats(global, multiMeasure, map);
    }

    static MultiMeasure createMultiMeasure(Measure global,
            LinkedHashMap<String, SingleTestStats> map) {
        Measure[] measures = extractMeasureArray(map.values());
        return new MultiMeasure(global, measures);
    }

    static Measure[] extractMeasureArray(Collection<SingleTestStats> collection) {
        Measure[] measures = new Measure[collection.size()];
        int index = 0;
        for (SingleTestStats tp : collection) {
            measures[index] = tp.getElapsedNanosecondsPerCycle();
            index++;
        }
        return measures;
    }

}
