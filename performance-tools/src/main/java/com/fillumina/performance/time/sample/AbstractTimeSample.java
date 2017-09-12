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
public abstract class AbstractTimeSample
        extends AbstractSample<AbstractTimeSample, TimeSampleValue, TimeStats> {
    protected static final long serialVersionUID = 1L;
    protected final long totalTimeNs;

    public AbstractTimeSample(
            TNameMap<TimeSampleValue> map,
            long totalTimeNs) {
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

    protected abstract TimeStats createStats(MultiMeasure multiMeasure,
            TNameMap<SingleTimeStats> testStatsMap);

    @Override
    public StatsBuilder<TimeStats, AbstractTimeSample> getStatsBuilder() {
        return new StatsBuilderImpl.Creator<>(
                ()-> new TimeSampleValueAccumulator(),

                (TimeSampleValueAccumulator a, TimeSampleValue v) -> {
                    a.totalIterations += v.getIterations();
                    a.totalTimeNs += v.getTimeNs();
                    return true;
                },

                (CollectedMeasures<TimeSampleValueAccumulator> m) ->
                    createStats(
                            m.getMultiMeasure(),
                            m.getSingleStatsMap(
                                (TimeSampleValueAccumulator a) ->
                                    new SingleTimeStats(a.getName(),
                                            a.getMeasure(),
                                            a.totalIterations,
                                            a.getValues().size(),
                                            a.totalTimeNs) ))
                );
    }

}
