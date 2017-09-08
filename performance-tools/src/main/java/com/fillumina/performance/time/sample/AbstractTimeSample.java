package com.fillumina.performance.time.sample;

import com.fillumina.performance.infrastructure.sample.AbstractSample;
import com.fillumina.performance.infrastructure.sample.CollectedMeasures;
import com.fillumina.performance.infrastructure.sample.SampleValueAccumulator;
import com.fillumina.performance.infrastructure.sample.StatsBuilder;
import com.fillumina.performance.infrastructure.sample.StatsBuilderImpl;
import com.fillumina.performance.time.stats.SingleTimeStats;
import com.fillumina.performance.time.stats.TimeStats;
import com.fillumina.performance.util.stats.MultiMeasure;
import com.fillumina.performance.util.tname.TNameMap;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class AbstractTimeSample<S extends TimeStats>
        extends AbstractSample<AbstractTimeSample<S>, TimeSampleValue, S> {
    protected static final long serialVersionUID = 1L;
    protected final long totalTimeNs;

    public AbstractTimeSample(TNameMap<TimeSampleValue> map, long totalTimeNs) {
        super(map);
        this.totalTimeNs = totalTimeNs;
    }

    /**
     * @return the time spent in all the iterations of all the tests in
     * the sample (nanoseconds).
     */
    public long getTotalTimeNs() {
        return totalTimeNs;
    }

    private static class TimeSampleValueAccumulator
            extends SampleValueAccumulator {
        long totalIterations;
        long totalTimeNs;
    }

    protected abstract S createStats(MultiMeasure multiMeasure,
            TNameMap<SingleTimeStats> testStatsMap);

    @Override
    public StatsBuilder<S, AbstractTimeSample<S>> getStatsBuilder() {
        return new StatsBuilderImpl.Creator<S,
                                            AbstractTimeSample<S>,
                                            TimeSampleValue,
                                            TimeSampleValueAccumulator>(
                ()-> new TimeSampleValueAccumulator(),

                (TimeSampleValueAccumulator a, TimeSampleValue v) -> {
                    a.totalIterations += v.getIterations();
                    a.totalTimeNs += v.getTimeNs();
                },

                (CollectedMeasures<TimeSampleValueAccumulator> m) ->
                    createStats(
                            m.getMultiMeasure(),
                            m.getSingleStatsList(
                                (TimeSampleValueAccumulator a) ->
                                    new SingleTimeStats(a.getTestName(),
                                            a.getMeasure(),
                                            a.totalIterations,
                                            a.getValues().size(),
                                            a.totalTimeNs) ))
                );
    }

}
