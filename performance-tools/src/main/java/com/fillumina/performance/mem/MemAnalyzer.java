package com.fillumina.performance.mem;

import com.fillumina.performance.executor.AbstractNamedTestExecutor;
import com.fillumina.performance.executor.AssertableHolder;
import com.fillumina.performance.executor.MixedAssertableHolder;
import com.fillumina.performance.mem.stats.AllocatedMemStats;
import com.fillumina.performance.mem.stats.MemStatsProducer;
import com.fillumina.performance.mem.stats.UsedMemStats;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MemAnalyzer {

    public static long used(Runnable runnable) {
        MixedAssertableHolder mixed =
                MemStatsProducer.createUsed()
                .addTest(runnable)
                .execute();
        AssertableHolder<UsedMemStats> holder = mixed.getStats();

        return (long) holder.getAssertable()
                .getMeasure(AbstractNamedTestExecutor.SINGLE_TEST_NAME)
                .getMean();
    }

    public static long allocated(Runnable runnable) {
        MixedAssertableHolder mixed =
                MemStatsProducer.createAllocated()
                .addTest(runnable)
                .execute();
        AssertableHolder<AllocatedMemStats> holder = mixed.getStats();

        return (long) holder.getAssertable()
                .getMeasure(AbstractNamedTestExecutor.SINGLE_TEST_NAME)
                .getMean();
    }
}
