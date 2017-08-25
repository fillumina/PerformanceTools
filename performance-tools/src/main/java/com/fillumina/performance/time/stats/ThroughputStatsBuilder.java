package com.fillumina.performance.time.stats;

import com.fillumina.performance.time.sample.IterationTime;
import com.fillumina.performance.util.tname.TName;
import com.fillumina.performance.util.stats.MultiMeasure;
import com.fillumina.performance.util.stats.OnlineMeasure;
import com.fillumina.performance.util.unit.DimensionalOnlineMeasure;
import com.fillumina.performance.util.unit.ThroughputUnit;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Builds a {@link ThroghputStats} out of collected {@link SingleTimeStats}.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ThroughputStatsBuilder
        implements TimeStatsBuilder<ThroughputStats> {

    private final LinkedHashMap<TName, SingleTimeStats> map;
    private final OnlineMeasure global = new OnlineMeasure();

    public ThroughputStatsBuilder() {
        this(16);
    }

    /** @param testCount the number of tests. */
    public ThroughputStatsBuilder(int testCount) {
        this.map = new LinkedHashMap<>(testCount);
    }

    /**
     * Adds the samples relative to the named test. Because the samples could
     * have been filtered to eliminate outliers it records the original samples
     * number too.
     *
     * @param name          test's name
     * @param totalSamples  total number of samples collected (including filtered
     *                      ones)
     * @param samples       samples
     */
    @Override
    public void add(TName name,
            int totalSamples,
            List<IterationTime> samples) {
        long totalIterations = 0;
        long totalTime = 0;
        DimensionalOnlineMeasure measure =
                new DimensionalOnlineMeasure(ThroughputUnit.UNIT);

        for (IterationTime it : samples) {
            totalIterations += it.getIterations();
            totalTime += it.getTimeNs();

            final double frequency = it.getFrequency();
            measure.add(frequency);
            global.add(frequency);
        }

        SingleTimeStats singleTestStats =
                new SingleTimeStats(name, measure, totalIterations,
                        samples.size(), totalSamples, totalTime);

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
    public ThroughputStats build() {
        MultiMeasure multiMeasure =
                TimeStatsBuilder.createMultiMeasure(global, map);
        return new ThroughputStats(multiMeasure, map);
    }
}
