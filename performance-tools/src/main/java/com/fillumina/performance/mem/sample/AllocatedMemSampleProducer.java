package com.fillumina.performance.mem.sample;

import com.fillumina.performance.mem.stats.AllocatedMemStats;
import com.fillumina.performance.sample.SampleValue;
import com.fillumina.performance.util.tname.TNameMap;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AllocatedMemSampleProducer
        extends AbstractMemSampleProducer<AllocatedMemSample, AllocatedMemStats> {

    @Override
    protected AllocatedMemSample createSample(TNameMap<SampleValue> map) {
        return new AllocatedMemSample(map);
    }

    /**
     * @return how much memory {@link Testable} has allocated.
     */
    @Override
    public long execute(Runnable runnable) {
        return AllocatedMemSampleExecutor.INSTANCE.execute(runnable);
    }
}
