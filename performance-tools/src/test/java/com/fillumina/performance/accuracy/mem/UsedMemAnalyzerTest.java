package com.fillumina.performance.accuracy.mem;

import com.fillumina.performance.assertion.Assertion;
import com.fillumina.performance.assertion.Assertions;
import com.fillumina.performance.executor.AssertableHolder;
import com.fillumina.performance.mem.stats.MemStats;
import com.fillumina.performance.mem.stats.MemStatsProducer;
import com.fillumina.performance.executor.test.SafeSink;
import com.fillumina.performance.util.stats.Ratio;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class UsedMemAnalyzerTest {
    private static final String NOMEMORY = "nomemory";
    private static final String ARRAY = "array";

    private static AssertableHolder<MemStats> MEMSTATS_HOLDER;

    @BeforeClass
    public static void initMemStats() {
        MEMSTATS_HOLDER = MemStatsProducer.createUsed()
                .addTest(NOMEMORY, (Runnable) () -> { SafeSink.drain(null); })
                .addTest(ARRAY, (Runnable) () -> { SafeSink.drain(new int[10]); })
                .execute()
                .getStats();
    }

    @Test
    public void shouldCheckMultipleAssertion() {
        Assertion assertion =
                Assertions.withTolerance(Ratio.percentage(10))
                .assertValue(NOMEMORY).sameAs(0)
                .assertValue(ARRAY).sameAs(16 + 4 * 10)
                .assertOrder(NOMEMORY).lessThan(ARRAY);

        MEMSTATS_HOLDER.check(assertion);
    }

    @Test(expected = AssertionError.class)
    public void shouldNotAssertWrongOrder() {
        Assertion assertion =
                Assertions.withTolerance(Ratio.percentage(10))
                .assertOrder(NOMEMORY).sameAs(ARRAY);

        MEMSTATS_HOLDER.check(assertion);
    }

    @Test
    public void shouldAssertValueWithinTolerance() {
        Assertion assertion =
                Assertions.withTolerance(Ratio.percentage(10))
                .assertValue(ARRAY).sameAs(16 + 4 * 10 + 1);

        MEMSTATS_HOLDER.check(assertion);
    }

    @Test(expected = AssertionError.class)
    public void shouldNotAssertValueOutsideTolerance() {
        Assertion assertion =
                Assertions.withTolerance(Ratio.percentage(10))
                .assertValue(ARRAY).sameAs(16 + 4 * 10 + 10);

        MEMSTATS_HOLDER.check(assertion);
    }
}
