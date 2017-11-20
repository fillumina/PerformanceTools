package com.fillumina.performance.mem;

import com.fillumina.performance.executor.AbstractTestExecutor;
import com.fillumina.performance.executor.AssertableHolder;
import com.fillumina.performance.executor.MixedAssertableHolder;
import com.fillumina.performance.executor.stats.Stats;
import com.fillumina.performance.mem.stats.MemStatsProducer;

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
        AssertableHolder<Stats> holder = mixed.getStats();

        return (long) holder.getAssertable()
                .getMeasure(AbstractTestExecutor.SINGLE_TEST_NAME)
                .getMean();
    }

    public static long allocated(Runnable runnable) {
        MixedAssertableHolder mixed =
                MemStatsProducer.createAllocated()
                .addTest(runnable)
                .execute();
        AssertableHolder<Stats> holder = mixed.getStats();

        return (long) holder.getAssertable()
                .getMeasure(AbstractTestExecutor.SINGLE_TEST_NAME)
                .getMean();
    }
}
