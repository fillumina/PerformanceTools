package com.fillumina.performance.time.stats;

import com.fillumina.performance.time.stats.strgen.TimeStatsStringGeneratorSelector;
import com.fillumina.performance.util.TName;
import com.fillumina.performance.util.stats.MultiMeasure;
import java.util.LinkedHashMap;

/**
 * Statistics about the average time (average time per operation) of a group of
 * tests.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AverageTimeStats extends TimeStats {
    private static final long serialVersionUID = 1L;

    public AverageTimeStats(MultiMeasure multiMeasure,
            LinkedHashMap<TName, SingleTimeStats> testStatsMap) {
        super(multiMeasure, testStatsMap);
    }

    @Override
    protected AverageTimeStats createNew(MultiMeasure multiMeasure,
            LinkedHashMap<TName, SingleTimeStats> testStatsMap) {
        return new AverageTimeStats(multiMeasure, testStatsMap);
    }

    @Override
    public String toString() {
        return TimeStatsStringGeneratorSelector.AVERAGE_TIME.toString(this);
    }
}
