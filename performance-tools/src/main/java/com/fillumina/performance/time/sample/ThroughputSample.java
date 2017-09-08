package com.fillumina.performance.time.sample;

import com.fillumina.performance.time.stats.SingleTimeStats;
import com.fillumina.performance.time.stats.ThroughputStats;
import com.fillumina.performance.util.stats.MultiMeasure;
import com.fillumina.performance.util.tname.TNameMap;

/**
 *
 * @author Francesco Illuminati
 */
public class ThroughputSample
        extends AbstractTimeSample<ThroughputStats> {
    private static final long serialVersionUID = 1L;

    public ThroughputSample(TNameMap<TimeSampleValue> map, long totalTimeNs) {
        super(map, totalTimeNs);
    }

    @Override
    protected ThroughputStats createStats(MultiMeasure multiMeasure,
            TNameMap<SingleTimeStats> testStatsMap) {
        return new ThroughputStats(multiMeasure, testStatsMap);
    }

}
