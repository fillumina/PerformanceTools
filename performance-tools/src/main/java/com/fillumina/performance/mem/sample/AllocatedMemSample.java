package com.fillumina.performance.mem.sample;

import com.fillumina.performance.sample.SampleValue;
import com.fillumina.performance.sample.StatsBuilder;
import com.fillumina.performance.sample.SampleValueAccumulator;
import com.fillumina.performance.stats.SingleStats;
import com.fillumina.performance.sample.StatsBuilderImpl;
import com.fillumina.performance.mem.stats.AllocatedMemStats;
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
