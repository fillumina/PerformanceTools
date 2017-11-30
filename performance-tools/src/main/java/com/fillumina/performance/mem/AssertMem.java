package com.fillumina.performance.mem;

import com.fillumina.performance.executor.AbstractTestExecutor;
import com.fillumina.performance.executor.stats.MixedStatsHolder;
import com.fillumina.performance.executor.stats.StatsHolder;
import com.fillumina.performance.mem.stats.MemStatsProducer;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AssertMem {

    public static void used(long expected, Runnable runnable) {
        MixedStatsHolder mixed =
                MemStatsProducer.createUsed()
                .addTest(runnable)
                .execute();
        StatsHolder holder = mixed.getFirstStatsHolder();

        holder.check().value()
                .string(AbstractTestExecutor.SINGLE_TEST_NAME)
                .equalsTo(expected).end();
    }

    public static void allocated(long expected, Runnable runnable) {
        MixedStatsHolder mixed =
                MemStatsProducer.createAllocated()
                .addTest(runnable)
                .execute();
        StatsHolder holder = mixed.getFirstStatsHolder();

        holder.check().value()
                .string(AbstractTestExecutor.SINGLE_TEST_NAME)
                .equalsTo(expected).end();
    }
}
