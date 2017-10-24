package com.fillumina.performance.time.stats;

import com.fillumina.performance.time.stats.strgen.TimeStatsStringGeneratorSelector;
import com.fillumina.performance.util.stats.Significance;
import com.fillumina.performance.util.tname.TNameMap;

/**
 * Statistics about the throughput (operations per unit of time) of a group
 * of tests.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ThroughputStats extends TimeStats {
    private static final long serialVersionUID = 1L;

    public ThroughputStats(Significance multiMeasure,
            TNameMap<SingleTimeStats> singleStatsMap) {
        super(multiMeasure, singleStatsMap);
    }

    @Override
    public String toString() {
        return TimeStatsStringGeneratorSelector.THROUGHPUT.toString(this);
    }
}
