package com.fillumina.performance.mem.sample;

import com.fillumina.performance.executor.stats.Stats;
import com.fillumina.performance.executor.stats.StatsType;
import com.fillumina.performance.mem.MemStatsType;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AllocatedMemSampleProducer extends AbstractMemSampleProducer {

    @Override
    public StatsType getStatsType() {
        return MemStatsType.ALLOCATED;
    }

    /**
     * @return how much memory {@link Testable} has allocated.
     */
    @Override
    public long execute(Runnable runnable) {
        return AllocatedMemSampleExecutor.INSTANCE.execute(runnable);
    }
}
