package com.fillumina.performance.infrastructure.stats;

import com.fillumina.performance.infrastructure.sample.TestSample;
import com.fillumina.performance.time.stats.*;
import com.fillumina.performance.util.Builder;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.MultiMeasure;
import com.fillumina.performance.util.stats.OnlineMeasure;
import com.fillumina.performance.util.tname.TName;
import com.fillumina.performance.util.unit.DimensionalOnlineMeasure;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Builds a {@link TimeStats} out of collected {@link SingleStats}.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class StatsBuilder implements Builder<Stats> {

    private final LinkedHashMap<TName, SingleStats> map;
    private final OnlineMeasure global = new OnlineMeasure();

    public StatsBuilder() {
        this(16);
    }

    /** @param testCount the number of tests */
    public StatsBuilder(int testCount) {
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
    public void add(TName name,
            int requiredSamples,
            List<? extends TestSample> samples) {
        TestSample firstSample = samples.iterator().next();
        DimensionalOnlineMeasure measure =
                new DimensionalOnlineMeasure(firstSample.getUnit());

        for (TestSample s : samples) {
            final double value = s.getValue();
            measure.add(value);
            global.add(value);
        }

        SingleStats singleStats = null;
//                new SingleStats(name, measure, totalIterations,
//                        samples.size(), requiredSamples, totalTime);

        map.put(name, singleStats);
    }

    /* test */ LinkedHashMap<TName, SingleStats> getMap() {
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
    public Stats build() {
        MultiMeasure multiMeasure =
                StatsBuilder.createMultiMeasure(global, map);
        return new Stats(multiMeasure, null);
    }

    public static MultiMeasure createMultiMeasure(Measure global,
            LinkedHashMap<TName, SingleStats> map) {
        Measure[] measures = extractMeasureArray(map.values());
        return new MultiMeasure(global, measures);
    }

    public static Measure[] extractMeasureArray(
            Collection<SingleStats> collection) {
        Measure[] measures = new Measure[collection.size()];
        int index = 0;
        for (SingleStats tp : collection) {
            measures[index] = tp.getMeasure();
            index++;
        }
        return measures;
    }
}
