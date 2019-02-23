package com.fillumina.performance.mem.sample;

import com.fillumina.performance.executor.stats.StatsType;
import com.fillumina.performance.mem.MemStatsType;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class UsedMemSampleProducer extends AbstractMemSampleProducer {

    @Override
    public StatsType getStatsType() {
        return MemStatsType.USED;
    }

    /**
     * @return how much memory {@link Testable} has allocated.
     */
    @Override
    public long execute(Runnable runnable) {
        return UsedMemSampleExecutor.INSTANCE.execute(runnable);
    }
}
