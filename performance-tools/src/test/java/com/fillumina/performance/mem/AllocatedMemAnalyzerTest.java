package com.fillumina.performance.mem;

import com.fillumina.performance.infrastructure.PHolder;
import com.fillumina.performance.mem.sample.AllocatedMemConsumptionExecutor;
import com.fillumina.performance.infrastructure.AbstractTestable;
import com.fillumina.performance.infrastructure.Drain;
import com.fillumina.performance.util.stats.Ratio;
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

    private static final PHolder<MemStats> MEMSTATS =
            AllocatedMemConsumptionExecutor.createMemAnalyzer()
                .addTest(NOMEMORY, new AbstractTestable() {
                    @Override
                    public void test() {
                    }
                })
                .addTest(NOALLOCATED, new AbstractTestable() {
                    @Override
                    public void test() {
                        Drain.drain(new int[10]);
                    }
                })
                .addTest(ALLOCATED, new AbstractTestable() {
                    final List<Object> list = new ArrayList<>(100);
                    @Override
                    public void test() {
                        Drain.drain(list.add(new int[10]));
                    }
                })
                .execute();


    @Test
    public void shouldCheckMultipleAssertion() {
        AssertMemory.withTolerance(Ratio.ZERO)
                .assertValue(NOMEMORY).sameAs(0)
                .assertValue(NOALLOCATED).sameAs(0)
                .assertValue(ALLOCATED).sameAs(16 + 4 * 10)
                .assertOrder(NOMEMORY).sameAs(NOALLOCATED)
                .assertOrder(NOMEMORY).lessThan(ALLOCATED)
                .check(MEMSTATS);
    }

    @Test(expected = AssertionError.class)
    public void shouldNotAssertWrongOrder() {
        AssertMemory.withTolerance(Ratio.percentage(10))
                .assertOrder(NOALLOCATED).sameAs(ALLOCATED)
                .check(MEMSTATS);
    }

    @Test
    public void shouldAssertValueWithinTolerance() {
        AssertMemory.withTolerance(Ratio.percentage(10))
                .assertValue(ALLOCATED).sameAs(16 + 4 * 10 + 1)
                .check(MEMSTATS);
    }

    @Test(expected = AssertionError.class)
    public void shouldNotAssertValueOutsideTolerance() {
        AssertMemory.withTolerance(Ratio.percentage(10))
                .assertValue(ALLOCATED).sameAs(16 + 4 * 10 + 10)
                .check(MEMSTATS);
    }
}
