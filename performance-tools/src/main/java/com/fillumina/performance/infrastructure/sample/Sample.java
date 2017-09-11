package com.fillumina.performance.infrastructure.sample;

import com.fillumina.performance.infrastructure.stats.SingleStats;
import com.fillumina.performance.infrastructure.stats.Stats;
import com.fillumina.performance.util.tname.TNameMap;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class Sample
        extends AbstractSample<Sample,SampleValue,Stats<SingleStats>> {
    private static final long serialVersionUID = 1L;

    public Sample(TNameMap<SampleValue> map) {
        super(map);
    }

    @Override
    public StatsBuilder<Stats<SingleStats>,Sample> getStatsBuilder() {
        return new StatsBuilderImpl.Creator<>(
                ()-> new SampleValueAccumulator(),
                m -> new Stats<>(m.getMultiMeasure(),
                        m.getSingleStatsMap( a ->
                                new SingleStats(a.getTestName(), a.getMeasure()))));
    }

}
