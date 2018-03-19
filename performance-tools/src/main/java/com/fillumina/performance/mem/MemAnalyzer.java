package com.fillumina.performance.mem;

import com.fillumina.performance.executor.AbstractTestExecutor;
import com.fillumina.performance.executor.stats.MixedStatsHolder;
import com.fillumina.performance.executor.stats.StatsHolder;
import com.fillumina.performance.mem.stats.MemStatsProducer;
import com.fillumina.performance.util.unit.MemUnit;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MemAnalyzer {

    public static long used(Runnable runnable) {
        MixedStatsHolder mixed =
                MemStatsProducer.createUsed()
                .addTest(runnable)
                .execute();
        StatsHolder holder = mixed.getFirstStatsHolder();

        return (long) holder.getStats()
                .as(MemUnit.B)
                .getMeasure(AbstractTestExecutor.SINGLE_TEST_NAME)
                .getMean();
    }

    public static long allocated(Runnable runnable) {
        MixedStatsHolder mixed =
                MemStatsProducer.createAllocated()
                .addTest(runnable)
                .execute();
        StatsHolder holder = mixed.getFirstStatsHolder();

        return (long) holder.getStats()
                .as(MemUnit.B)
                .getMeasure(AbstractTestExecutor.SINGLE_TEST_NAME)
                .getMean();
    }
}
