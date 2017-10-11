package com.fillumina.performance.mem.sample;

import com.fillumina.performance.executor.sample.SampleValue;
import com.fillumina.performance.executor.stats.Stats;
import com.fillumina.performance.mem.stats.AllocatedMemStats;
import com.fillumina.performance.util.collection.ReadOnlyList;
import com.fillumina.performance.util.tname.TNameMap;
import java.util.Collection;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AllocatedMemSampleProducer
        extends AbstractMemSampleProducer<AllocatedMemSample, AllocatedMemStats> {

    @SuppressWarnings("unchecked")
    private static final ReadOnlyList<Class<? extends Stats<?>>> STATS =
            new ReadOnlyList<>(AllocatedMemStats.class);

    @Override
    public Collection<Class<? extends Stats<?>>> getStatsTypeProduced() {
        return STATS;
    }
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
