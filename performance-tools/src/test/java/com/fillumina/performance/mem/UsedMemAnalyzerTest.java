package com.fillumina.performance.mem;

import com.fillumina.performance.assertion.Assertion;
import com.fillumina.performance.infrastructure.PHolder;
import com.fillumina.performance.infrastructure.Sink;
import com.fillumina.performance.mem.sample.UsedMemConsumptionExecutor;
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

    private static PHolder<MemStats> MEMSTATS;

    @BeforeClass
    public static void initMemStats() {
        MEMSTATS = UsedMemConsumptionExecutor.createMemAnalyzer()
                .addTest(NOMEMORY, new Runnable() {
                    @Override
                    public void run() {
                        Sink.drain(null);
                    }
                })
                .addTest(ARRAY, new Runnable() {
                    @Override
                    public void run() {
                        Sink.drain(new int[10]);
                    }
                })
                .execute();
    }

    @Test
    public void shouldCheckMultipleAssertion() {
        Assertion<MemStats> assertion =
                AssertMemory.withTolerance(Ratio.percentage(10))
                .assertValue(NOMEMORY).sameAs(0)
                .assertValue(ARRAY).sameAs(16 + 4 * 10)
                .assertOrder(NOMEMORY).lessThan(ARRAY);

        MEMSTATS.check(assertion);
    }

    @Test(expected = AssertionError.class)
    public void shouldNotAssertWrongOrder() {
        Assertion<MemStats> assertion =
                AssertMemory.withTolerance(Ratio.percentage(10))
                .assertOrder(NOMEMORY).sameAs(ARRAY);

        MEMSTATS.check(assertion);
    }

    @Test
    public void shouldAssertValueWithinTolerance() {
        Assertion<MemStats> assertion =
                AssertMemory.withTolerance(Ratio.percentage(10))
                .assertValue(ARRAY).sameAs(16 + 4 * 10 + 1);

        MEMSTATS.check(assertion);
    }

    @Test(expected = AssertionError.class)
    public void shouldNotAssertValueOutsideTolerance() {
        Assertion<MemStats> assertion =
                AssertMemory.withTolerance(Ratio.percentage(10))
                .assertValue(ARRAY).sameAs(16 + 4 * 10 + 10);

        MEMSTATS.check(assertion);
    }
}
