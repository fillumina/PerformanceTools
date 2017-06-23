package com.fillumina.performance.time.stats;

import com.fillumina.performance.time.sample.IterationTime;
import com.fillumina.performance.util.TName;
import com.fillumina.performance.util.stats.MultiMeasure;
import com.fillumina.performance.util.stats.OnlineMeasure;
import com.fillumina.performance.util.unit.DimensionalOnlineMeasure;
import com.fillumina.performance.util.unit.FrequencyUnit;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Builds a {@link TimeStats} out of collected {@link SingleTimeStats}.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
class FreqStatsBuilder implements TimeStatsBuilder<FreqStats> {

    private final LinkedHashMap<TName, SingleTimeStats> map;
    private final OnlineMeasure global = new OnlineMeasure();

    public FreqStatsBuilder() {
        this(16);
    }

    /**
     *
     * @param testCount the number of tests
     */
    public FreqStatsBuilder(int testCount) {
        this.map = new LinkedHashMap<>(testCount);
    }

    /**
     * Adds the samples relative to the named test. Because the samples could
     * have been filtered to eliminate outliers it records the original samples
     * number too.
     *
     * @param name          test's name
     * @param requiredSamples  total number of samples collects (including filtered
     *                      ones)
     * @param samples       samples
     */
    @Override
    public void add(TName name,
            int requiredSamples,
            List<IterationTime> samples) {
        long totalIterations = 0;
        long totalTime = 0;
        DimensionalOnlineMeasure measure =
                new DimensionalOnlineMeasure(FrequencyUnit.UNIT);

        for (IterationTime it : samples) {
            totalIterations += it.getIterations();
            totalTime += it.getTimeNs();

            final double frequency = it.getFrequency();
            measure.add(frequency);
            global.add(frequency);
        }

        SingleTimeStats singleTestStats =
                new SingleTimeStats(name, measure, totalIterations,
                        samples.size(), requiredSamples, totalTime);

        map.put(name, singleTestStats);
    }

    /* test */ LinkedHashMap<TName, SingleTimeStats> getMap() {
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
    public FreqStats build() {
        MultiMeasure multiMeasure =
                TimeStatsBuilder.createMultiMeasure(global, map);
        return new FreqStats(multiMeasure, map);
    }
}
