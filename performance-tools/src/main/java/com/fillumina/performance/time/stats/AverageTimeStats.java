package com.fillumina.performance.time.stats;

import com.fillumina.performance.time.stats.strgen.TimeStatsStringGeneratorSelector;
import com.fillumina.performance.util.stats.MultiMeasure;
import com.fillumina.performance.util.tname.TNameMap;

/**
 * Statistics about the average time (average time per operation) of a group of
 * tests.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AverageTimeStats extends TimeStats {
    private static final long serialVersionUID = 1L;

    public AverageTimeStats(MultiMeasure multiMeasure,
            TNameMap<SingleTimeStats> singleStatsMap) {
        super(multiMeasure, singleStatsMap);
    }

    @Override
    public String toString() {
        return TimeStatsStringGeneratorSelector.AVERAGE_TIME.toString(this);
    }
}
