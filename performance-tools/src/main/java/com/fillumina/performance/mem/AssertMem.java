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
public class AssertMem {

    public static void used(long expected, Runnable runnable) {
        MixedAssertableHolder mixed =
                MemStatsProducer.createUsed()
                .addTest(runnable)
                .execute();
        AssertableHolder<Stats> holder = mixed.getStats();

        holder.check().value()
                .string(AbstractTestExecutor.SINGLE_TEST_NAME)
                .equalsTo(expected).end();
    }

    public static void allocated(long expected, Runnable runnable) {
        MixedAssertableHolder mixed =
                MemStatsProducer.createAllocated()
                .addTest(runnable)
                .execute();
        AssertableHolder<Stats> holder = mixed.getStats();

        holder.check().value()
                .string(AbstractTestExecutor.SINGLE_TEST_NAME)
                .equalsTo(expected).end();
    }
}
