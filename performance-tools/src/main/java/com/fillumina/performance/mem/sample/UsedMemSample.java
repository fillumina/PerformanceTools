package com.fillumina.performance.mem.sample;

import com.fillumina.performance.sample.SampleValue;
import com.fillumina.performance.sample.StatsBuilder;
import com.fillumina.performance.sample.SampleValueAccumulator;
import com.fillumina.performance.stats.SingleStats;
import com.fillumina.performance.sample.StatsBuilderImpl;
import com.fillumina.performance.mem.stats.UsedMemStats;
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
                            m.getSingleStatsMap( a ->
                                new SingleStats(a.getName(), a.getMeasure()))));
    }

}
