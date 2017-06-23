package com.fillumina.performance.time.stats;

import com.fillumina.performance.time.sample.IterationTime;
import com.fillumina.performance.util.Builder;
import com.fillumina.performance.util.TName;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.MultiMeasure;
import com.fillumina.performance.util.stats.OnlineMeasure;
import com.fillumina.performance.util.unit.DimensionalOnlineMeasure;
import com.fillumina.performance.util.unit.IntervalUnit;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Builds a {@link TimeStats} out of collected {@link SingleSpeedStats}.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
class TimeStatsBuilder implements Builder<TimeStats> {

    private final LinkedHashMap<TName, SingleSpeedStats> map;
    private final OnlineMeasure global = new OnlineMeasure();

    /**
     *
     * @param testCount the number of tests
     */
    public TimeStatsBuilder(int testCount) {
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
    public void add(TName name,
            int originalSamples,
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

        SingleSpeedStats singleTestStats =
                new SingleSpeedStats(name, timeMeasure, totalIterations,
                        samples.size(), originalSamples, totalTime);

        map.put(name, singleTestStats);
    }

    /* test */ LinkedHashMap<TName, SingleSpeedStats> getMap() {
        return map;
    }

    /* test */ OnlineMeasure getGlobal() {
        return global;
    }

    /**
     * Builds a {@link TimeStats} out of the collected samples.
     *
     * @param message       The message to add to the statistics
     * @param confidence    The confidence used
     * @return              The statistics computed over the collected samples
     */
    @Override
    public TimeStats build() {
        MultiMeasure multiMeasure = createMultiMeasure(global, map);
        return new TimeStats(multiMeasure, map);
    }

    static MultiMeasure createMultiMeasure(Measure global,
            LinkedHashMap<TName, SingleSpeedStats> map) {
        Measure[] measures = extractMeasureArray(map.values());
        return new MultiMeasure(global, measures);
    }

    static Measure[] extractMeasureArray(Collection<SingleSpeedStats> collection) {
        Measure[] measures = new Measure[collection.size()];
        int index = 0;
        for (SingleSpeedStats tp : collection) {
            measures[index] = tp.getElapsedNanosecondsPerCycle();
            index++;
        }
        return measures;
    }

}
