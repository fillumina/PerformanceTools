package com.fillumina.performance.time.sample;

import com.fillumina.performance.time.stats.AverageTimeStats;
import com.fillumina.performance.time.stats.SingleTimeStats;
import com.fillumina.performance.util.stats.Significance;
import com.fillumina.performance.util.tname.TNameMap;

/**
 *
 * @author Francesco Illuminati
 */
public class AverageTimeSample extends AbstractTimeSample {
    private static final long serialVersionUID = 1L;

    public AverageTimeSample(TNameMap<TimeSampleValue> map, long totalTimeNs) {
        super(map, totalTimeNs);
    }

    @Override
    protected AverageTimeStats createStats(Significance multiMeasure,
            TNameMap<SingleTimeStats> testStatsMap) {
        return new AverageTimeStats(multiMeasure, testStatsMap);
    }
}
