package com.fillumina.performance.mem.sample;

import com.fillumina.performance.infrastructure.sample.SampleValue;
import com.fillumina.performance.infrastructure.sample.StatsBuilder;
import com.fillumina.performance.infrastructure.sample.SampleValueAccumulator;
import com.fillumina.performance.infrastructure.stats.SingleStats;
import com.fillumina.performance.infrastructure.sample.StatsBuilderImpl;
import com.fillumina.performance.mem.AllocatedMemStats;
import com.fillumina.performance.util.tname.TNameMap;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AllocatedMemSample
        extends AbstractMemSample<AllocatedMemSample, AllocatedMemStats> {
    private static final long serialVersionUID = 1L;

    public AllocatedMemSample(TNameMap<SampleValue> map) {
        super(map);
    }

    @Override
    public StatsBuilder<AllocatedMemStats, AllocatedMemSample> getStatsBuilder() {
        return new StatsBuilderImpl.Creator<>(
                ()-> new SampleValueAccumulator(),
                m -> new AllocatedMemStats(m.getMultiMeasure(),
                        m.getSingleStatsMap( a ->
                                new SingleStats(a.getName(), a.getMeasure()))));
    }

}
