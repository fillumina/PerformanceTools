package com.fillumina.performance.time.sample;

import com.fillumina.performance.infrastructure.sample.AbstractSample;
import com.fillumina.performance.infrastructure.stats.SampleCollector;
import com.fillumina.performance.time.stats.SingleTimeStats;
import com.fillumina.performance.time.stats.TimeStats;
import com.fillumina.performance.util.tname.TNameMap;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class TimeSample extends AbstractSample<TimeSample, TimeSampleValue> {
    protected static final long serialVersionUID = 1L;
    protected final long totalTimeNs;

    public TimeSample(TNameMap<TimeSampleValue> map, long totalTimeNs) {
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

    @Override
    public SampleCollector<TimeStats, SingleTimeStats, TimeSample> getStatsCreator() {

    }
}
