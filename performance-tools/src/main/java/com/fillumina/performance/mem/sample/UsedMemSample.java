package com.fillumina.performance.mem.sample;

import com.fillumina.performance.infrastructure.sample.SampleValue;
import com.fillumina.performance.infrastructure.sample.StatsBuilder;
import com.fillumina.performance.infrastructure.sample.SampleValueAccumulator;
import com.fillumina.performance.infrastructure.stats.SingleStats;
import com.fillumina.performance.infrastructure.sample.StatsBuilderImpl;
import com.fillumina.performance.mem.UsedMemStats;
import com.fillumina.performance.util.tname.TNameMap;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class UsedMemSample
        extends AbstractMemSample<UsedMemSample, UsedMemStats> {
    private static final long serialVersionUID = 1L;

    public UsedMemSample(TNameMap<SampleValue> map) {
        super(map);
    }

    @Override
    public StatsBuilder<UsedMemStats, UsedMemSample> getStatsBuilder() {
        return new StatsBuilderImpl.Creator<>(
                ()-> new SampleValueAccumulator(),
                m -> new UsedMemStats(m.getMultiMeasure(),
                            m.getSingleStatsList( a ->
                                new SingleStats(a.getTestName(), a.getMeasure()))));
    }

}
