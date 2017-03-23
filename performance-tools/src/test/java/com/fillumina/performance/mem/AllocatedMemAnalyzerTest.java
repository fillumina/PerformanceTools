package com.fillumina.performance.mem;

import com.fillumina.performance.infrastructure.PHolder;
import com.fillumina.performance.mem.sample.AllocatedMemConsumptionExecutor;
import com.fillumina.performance.infrastructure.Testable;
import com.fillumina.performance.infrastructure.Sink;
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
                .addTest(NOMEMORY, new Testable() {
                    @Override
                    public void test() {
                    }
                })
                .addTest(NOALLOCATED, new Testable() {
                    @Override
                    public void test() {
                        Sink.drain(new int[10]);
                    }
                })
                .addTest(ALLOCATED, new Testable() {
                    final List<Object> list = new ArrayList<>(100);
                    @Override
                    public void test() {
                        Sink.drain(list.add(new int[10]));
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
