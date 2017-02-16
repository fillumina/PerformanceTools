package com.fillumina.performance.mem;

import com.fillumina.performance.infrastructure.PHolder;
import com.fillumina.performance.mem.sample.UsedMemConsumptionExecutor;
import com.fillumina.performance.speed.sample.AbstractTestable;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class UsedMemAnalyzerTest {
    private static final String NOMEMORY = "nomemory";
    private static final String ARRAY = "array";

    private static PHolder<MemStats> memStatsHolder;

    @BeforeClass
    public static void initMemStats() {
        memStatsHolder = UsedMemConsumptionExecutor.createMemAnalyzer()
                .addTest(NOMEMORY, new AbstractTestable() {
                    @Override
                    public Object test() {
                        return null;
                    }
                })
                .addTest(ARRAY, new AbstractTestable() {
                    @Override
                    public Object test() {
                        return new int[10];
                    }
                })
                .execute();
    }

    @Test
    public void shouldCheckMultipleAssertion() {
        AssertMemory.withTolerance(10)
                .assertValue(NOMEMORY).sameAs(0)
                .assertValue(ARRAY).sameAs(16 + 4 * 10)
                .assertOrder(NOMEMORY).lessThan(ARRAY)
                .check(memStatsHolder);
    }

    @Test(expected = AssertionError.class)
    public void shouldNotAssertWrongOrder() {
        AssertMemory.withTolerance(10)
                .assertOrder(NOMEMORY).sameAs(ARRAY)
                .check(memStatsHolder);
    }

    @Test
    public void shouldAssertValueWithinTolerance() {
        AssertMemory.withTolerance(10)
                .assertValue(ARRAY).sameAs(16 + 4 * 10 + 1)
                .check(memStatsHolder);
    }

    @Test(expected = AssertionError.class)
    public void shouldNotAssertValueOutsideTolerance() {
        AssertMemory.withTolerance(10)
                .assertValue(ARRAY).sameAs(16 + 4 * 10 + 10)
                .check(memStatsHolder);
    }
}
