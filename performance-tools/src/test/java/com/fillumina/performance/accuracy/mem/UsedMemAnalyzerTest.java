package com.fillumina.performance.accuracy.mem;

import com.fillumina.performance.assertion.Assertions;
import com.fillumina.performance.assertion.ExperimentAssertion;
import com.fillumina.performance.executor.stats.StatsHolder;
import com.fillumina.performance.executor.test.Sink;
import com.fillumina.performance.mem.stats.MemStatsProducer;
import com.fillumina.performance.util.stats.Ratio;
import com.fillumina.performance.util.unit.MemUnit;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class UsedMemAnalyzerTest {
    private static final String NOMEMORY = "nomemory";
    private static final String ARRAY = "array";

    private static StatsHolder MEMSTATS_HOLDER;

    @BeforeClass
    public static void initMemStats() {
        MEMSTATS_HOLDER = MemStatsProducer.createUsed()
                .addTest(NOMEMORY, (Runnable) () -> { Sink.drain(null); })
                .addTest(ARRAY, (Runnable) () -> { Sink.drain(new int[10]); })
                .execute()
                .getFirstStatsHolder();
    }

    @Test
    public void shouldCheckMultipleAssertion() {
        ExperimentAssertion assertion =
                Assertions.withTolerance(Ratio.percentage(10))
                .assertValue(NOMEMORY).equalsTo(MemUnit.B.quantity(0))
                .assertValue(ARRAY).equalsTo(MemUnit.B.quantity(16 + 4 * 10))
                .assertOrder(NOMEMORY).lessThan(ARRAY);

        MEMSTATS_HOLDER.check(assertion);
    }

    @Test(expected = AssertionError.class)
    public void shouldNotAssertWrongOrder() {
        ExperimentAssertion assertion =
                Assertions.withTolerance(Ratio.percentage(10))
                .assertOrder(NOMEMORY).sameAs(ARRAY);

        MEMSTATS_HOLDER.check(assertion);
    }

    @Test
    public void shouldAssertValueWithinTolerance() {
        ExperimentAssertion assertion =
                Assertions.withTolerance(Ratio.percentage(10))
                .assertValue(ARRAY).equalsTo(MemUnit.B.quantity(16 + 4 * 10 + 1));

        MEMSTATS_HOLDER.check(assertion);
    }

    @Test(expected = AssertionError.class)
    public void shouldNotAssertValueOutsideTolerance() {
        ExperimentAssertion assertion =
                Assertions.withTolerance(Ratio.percentage(10))
                .assertValue(ARRAY).equalsTo(MemUnit.B.quantity(16 + 4 * 10 + 10));

        MEMSTATS_HOLDER.check(assertion);
    }
}
