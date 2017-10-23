package com.fillumina.performance.mem.sample;

import com.fillumina.performance.executor.sample.SampleValue;
import com.fillumina.performance.mem.stats.UsedMemStats;
import com.fillumina.performance.util.tname.TNameMap;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class UsedMemSampleProducer
        extends AbstractMemSampleProducer<UsedMemSample, UsedMemStats> {

    @Override
    protected UsedMemSample createSample(TNameMap<SampleValue> map) {
        return new UsedMemSample(map);
    }

    /**
     * @return how much memory {@link Testable} has allocated.
     */
    @Override
    public long execute(Runnable runnable) {
        return UsedMemSampleExecutor.INSTANCE.execute(runnable);
    }
}
