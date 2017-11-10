package com.fillumina.performance.mock;

import com.fillumina.performance.executor.sample.AbstractSample;
import com.fillumina.performance.executor.sample.CollectedMeasures;
import com.fillumina.performance.executor.sample.SampleValue;
import com.fillumina.performance.executor.sample.SampleValueAccumulator;
import com.fillumina.performance.executor.sample.StatsBuilder;
import com.fillumina.performance.executor.sample.StatsBuilderImpl;
import com.fillumina.performance.executor.stats.SingleStats;
import com.fillumina.performance.executor.stats.Stats;
import com.fillumina.performance.util.stats.MultiMeasureSignificance;
import com.fillumina.performance.util.tname.TNameMap;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class AbstractSampleMock<I extends AbstractSampleMock<I,S>,
                                         S extends Stats<SingleStats>>
        extends AbstractSample<I, SampleValue, S> {
    private static final long serialVersionUID = 1L;

    public AbstractSampleMock(TNameMap<SampleValue> map) {
        super(map);
    }

    @Override
    public StatsBuilder<S,I> getStatsBuilder() {
        return new StatsBuilderImpl.Creator<>(
                () -> new SampleValueAccumulator(),
                (CollectedMeasures<SampleValueAccumulator> m) ->
                        createStats(m.getSignificance(),
                            m.getSingleStatsMap( (SampleValueAccumulator a) ->
                                new SingleStats(a.getName(), a.getMeasure()))));
    }

    protected abstract S createStats(
            MultiMeasureSignificance s,
            TNameMap<SingleStats> map);
}
