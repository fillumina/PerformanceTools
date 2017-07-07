package com.fillumina.performance.time.stats;

import com.fillumina.performance.time.stats.strgen.TimeStatsStringGeneratorSelector;
import com.fillumina.performance.util.TName;
import com.fillumina.performance.util.stats.MultiMeasure;
import java.util.LinkedHashMap;

/**
 * Statistics about the throughput (operations per unit of time) of a group
 * of tests.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ThroughputStats extends TimeStats {
    private static final long serialVersionUID = 1L;

    public ThroughputStats(MultiMeasure multiMeasure,
            LinkedHashMap<TName, SingleTimeStats> testStatsMap) {
        super(multiMeasure, testStatsMap);
    }

    @Override
    protected ThroughputStats createNew(MultiMeasure multiMeasure,
            LinkedHashMap<TName, SingleTimeStats> testStatsMap) {
        return new ThroughputStats(multiMeasure, testStatsMap);
    }

    @Override
    public String toString() {
        return TimeStatsStringGeneratorSelector.THROUGHPUT.toString(this);
    }
}
