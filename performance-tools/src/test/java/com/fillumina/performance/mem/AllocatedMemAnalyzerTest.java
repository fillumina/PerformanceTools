package com.fillumina.performance.mem;

import com.fillumina.performance.mem.sample.AllocatedMemConsumptionExecutor;
import com.fillumina.performance.speed.sample.AbstractTestable;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AllocatedMemAnalyzerTest {
    private static final String NOMEMORY = "nomemory";
    private static final String NOALLOCATED = "noallocated";
    private static final String ALLOCATED = "allocated";

    private final MemStats memStats;

    public AllocatedMemAnalyzerTest() {
        final List<Object> list = new ArrayList<>(100);
        memStats = AllocatedMemConsumptionExecutor.createMemAnalyzer()
                .addTest(NOMEMORY, new AbstractTestable() {
                    @Override
                    public Object test() {
                        return null;
                    }
                })
                .addTest(NOALLOCATED, new AbstractTestable() {
                    @Override
                    public Object test() {
                        return new int[10];
                    }
                })
                .addTest(ALLOCATED, new AbstractTestable() {
                    @Override
                    public Object test() {
                        return list.add(new int[10]);
                    }
                })
                .execute()
                .getPerformance();
    }

    @Test
    public void shouldCheckMultipleAssertion() {
        AssertMemory.withTolerance(10)
                .assertValue(NOMEMORY).sameAs(0)
                .assertValue(NOALLOCATED).sameAs(0)
                .assertValue(ALLOCATED).sameAs(16 + 4 * 10)
                .assertOrder(NOMEMORY).sameAs(NOALLOCATED)
                .assertOrder(NOMEMORY).lessThan(ALLOCATED)
                .check(memStats);
    }

    @Test(expected = AssertionError.class)
    public void shouldNotAssertWrongOrder() {
        AssertMemory.withTolerance(10)
                .assertOrder(NOALLOCATED).sameAs(ALLOCATED)
                .check(memStats);
    }

    @Test
    public void shouldAssertValueWithinTolerance() {
        AssertMemory.withTolerance(10)
                .assertValue(ALLOCATED).sameAs(16 + 4 * 10 + 1)
                .check(memStats);
    }

    @Test(expected = AssertionError.class)
    public void shouldNotAssertValueOutsideTolerance() {
        AssertMemory.withTolerance(10)
                .assertValue(ALLOCATED).sameAs(16 + 4 * 10 + 10)
                .check(memStats);
    }
}
