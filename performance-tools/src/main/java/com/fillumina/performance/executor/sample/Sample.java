package com.fillumina.performance.executor.sample;

import com.fillumina.performance.executor.stats.SingleStats;
import com.fillumina.performance.executor.stats.Stats;
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
                () -> new SampleValueAccumulator(),
                (CollectedMeasures<SampleValueAccumulator> m) -> new Stats<>(
                        m.getSignificance(),
                        m.getSingleStatsMap( (SampleValueAccumulator a) ->
                                new SingleStats(a.getName(), a.getMeasure()))));
    }

}
